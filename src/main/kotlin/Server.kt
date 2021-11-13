import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar


object Players : Table<Nothing>("players") {
    val id = int("id").primaryKey()
    val username = varchar("username")
    val points = int("points")
    val saved_at = datetime("saved_at")
}

data class Player(val id: Int?,val username: String?,val points: Int?,val saved_at: String?)

fun Application.module() {

    val database = Database.connect(
        url = "jdbc:mysql://localhost:3306/snake",
        user = "root",
        password = "root"
    )

    val builder = GsonBuilder()

    builder.serializeNulls()

    val gsonSerializer = builder.create()
    val gsonDeSerializer = Gson()

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    install(Routing) {
        get("/") {
            val data: MutableList<Player> = ArrayList()
            for (row in database.from(Players).select().limit(0, 10).orderBy(Players.points.desc())) {
                data.add(Player(row[Players.id], row[Players.username], row[Players.points], row[Players.saved_at].toString()))
            }
            val jsonData = gsonSerializer.toJson(data)
            call.respondText(jsonData, ContentType.Application.Json, HttpStatusCode.OK)
        }
        post("/") {
            val requestBody = call.receiveText()
            val parsedRequestBody = gsonDeSerializer.fromJson(requestBody, Player::class.java)
            val jsonData = gsonSerializer.toJson(parsedRequestBody)
            call.respondText(jsonData, ContentType.Application.Json, HttpStatusCode.OK)
        }
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::module).start(wait = true)
}