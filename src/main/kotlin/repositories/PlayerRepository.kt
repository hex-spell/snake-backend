package repositories

import entities.PlayerEntity
import entities.PlayerEntitySchema
import org.ktorm.database.Database
import org.ktorm.dsl.*

class PlayerRepository(database: Database, playerTable: PlayerEntitySchema) {
    private val database = database
    private val playersTable = playerTable

    fun fetchTheBest(): List<PlayerEntity.DTO> {
        return database.from(playersTable).select().limit(0, 10).orderBy(playersTable.points.desc()).map { row ->
            PlayerEntity.DTO(
                row[playersTable.id],
                row[playersTable.username],
                row[playersTable.points],
                row[playersTable.saved_at].toString()
            )
        }
    }

    fun fetchById(id: Int): PlayerEntity.DTO {
        return database.from(playersTable).select().where { playersTable.id eq id }.map { row ->
            PlayerEntity.DTO(
                row[playersTable.id],
                row[playersTable.username],
                row[playersTable.points],
                row[playersTable.saved_at].toString()
            )
        }[0]
    }

    fun insert(newPlayer: PlayerEntity.DTO): Int {
        val id =  database.insertAndGenerateKey(playersTable) {
            set(it.username, newPlayer.username)
            set(it.points, newPlayer.points)
        }
        if (id !is Int) {
            throw Exception("could not insert to database")
        }
        else return id
    }
}