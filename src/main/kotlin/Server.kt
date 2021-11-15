import com.google.gson.Gson
import com.google.gson.GsonBuilder
import config.Environment
import entities.PlayerEntity
import entities.PlayerEntitySchema
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import middleware.RateLimit
import org.ktorm.database.Database
import redis.clients.jedis.Jedis
import repositories.PlayerRepository
import kotlin.reflect.full.memberProperties

val env = Environment()

fun Application.module() {

    for (prop in Environment::class.memberProperties) {
        println("${prop.name} : ${prop.get(env)}")
    }

    val database = Database.connect(
        url = "jdbc:mysql://${env.dbHost}:${env.dbPort}/${env.dbName}?useSSL=false",
        user = env.dbUser,
        password = env.dbPassword
    )

    println("db ready")

    val jedisInstance = Jedis(env.redisHost, env.redisPort)

    println("jedis ready")

    val builder = GsonBuilder()

    builder.serializeNulls()

    val gsonSerializer = builder.create()
    val gsonDeSerializer = Gson()

    println("gson ready")

    val playersRepository = PlayerRepository(database, PlayerEntitySchema)

    println("repository ready")

    install(RateLimit) {
        jedis = jedisInstance
    }

    println("RateLimit ready")

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    println("ContentNegotiation ready")

    install(Routing) {
        get("/") {
            println("hello!")
            val cachedScores = jedisInstance.get("cached_scores")
            val response = if (cachedScores != null) {
                cachedScores
            } else {
                val data = playersRepository.fetchTheBest()
                val jsonData = gsonSerializer.toJson(data)
                jedisInstance.set("cached_scores", jsonData)
                jsonData
            }

            call.respondText(response, ContentType.Application.Json, HttpStatusCode.OK)
        }
        get("/snake-backend") {
            print("asdasd")
            call.respondText("response", ContentType.Application.Json, HttpStatusCode.OK)
        }
        post("/") {
            println("hello!")
            try {
                val requestBody = call.receiveText()
                val newPlayer = gsonDeSerializer.fromJson(requestBody, PlayerEntity.DTO::class.java)

                if (!PlayerEntity.isValid(newPlayer)) {
                    throw Exception("user is invalid")
                }

                val id = playersRepository.insert(newPlayer)

                val insertedPlayer = playersRepository.fetchById(id)

                val jsonData = gsonSerializer.toJson(insertedPlayer)

                // clean cache
                jedisInstance.del("cached_scores")

                call.respondText(jsonData, ContentType.Application.Json, HttpStatusCode.OK)
            } catch (error: Exception) {
                println(error)
                call.respondText(error.message ?: "Unhandled error", status = HttpStatusCode.BadRequest)
            }
        }
    }
}

fun main() {
    embeddedServer(Netty, port = 80, host = env.host, module = Application::module).start(wait = true)
}