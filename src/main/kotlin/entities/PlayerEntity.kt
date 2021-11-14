package entities

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object PlayerEntitySchema : Table<Nothing>("players") {
    val id = int("id").primaryKey()
    val username = varchar("username")
    val points = int("points")
    val saved_at = datetime("saved_at")
}

class PlayerEntity {
    data class DTO(var id: Int?, var username: String?, var points: Int?, var saved_at: String?)

    companion object {
        fun isValid(player: DTO): Boolean {
            var isInRange = false
            val isNotNull: Boolean = !(player.points == null || player.username == null)
            if (isNotNull) {
                isInRange =
                    !(player.points.toString().length > 30 || player.username!!.length > 16 || player.username!!.length < 3)
            }
            return isNotNull && isInRange
        }
    }
}