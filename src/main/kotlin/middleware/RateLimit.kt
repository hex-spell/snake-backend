package middleware

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import redis.clients.jedis.Jedis

// redis rate limit idea from here: https://engineering.classdojo.com/blog/2015/02/06/rolling-rate-limiter/
class RateLimit(configuration: Configuration) {
    private val jedis = configuration.jedis // Copies a snapshot of the mutable config into an immutable property.
    private val interval = configuration.interval
    private val quantity = configuration.quantity

    class Configuration() {
        var jedis: Jedis? = null
        var interval: Int = 60000
        var quantity: Int = 100
    }

    suspend fun intercept(context: PipelineContext<Unit, ApplicationCall>) {
        if (jedis is Jedis){
            val userIp = context.call.request.origin.remoteHost
            jedis.zremrangeByScore(userIp, 0.0, System.currentTimeMillis().toDouble()-interval)
            jedis.zadd(userIp, System.currentTimeMillis().toDouble(), System.currentTimeMillis().toString())
            jedis.expire(userIp, interval.toLong())

            val userUsageTokens = jedis.zrange(userIp, 0, -1)
            if (userUsageTokens.size > quantity) {
                context.call.respondText("too many requests", status = HttpStatusCode.Forbidden)
            }
        }
    }

    // Implements ApplicationFeature as a companion object.
    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, RateLimit> {
        // Creates a unique key for the feature.
        override val key = AttributeKey<RateLimit>("MyLimiter")

        // Code to execute when installing the plugin.
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): RateLimit {

            // It is responsibility of the install code to call the `configure` method with the mutable configuration.
            val configuration = Configuration().apply(configure)

            // Create the plugin, providing the mutable configuration so the plugin reads it keeping an immutable copy of the properties.
            val feature = RateLimit(configuration)

            // Intercept a pipeline.
            pipeline.intercept(phase = ApplicationCallPipeline.Call) {
                feature.intercept(this)
            }
            return feature
        }
    }
}