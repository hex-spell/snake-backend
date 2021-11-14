package config

class Environment {
    val dbHost = System.getenv("DB_HOST") ?: "localhost"
    val dbName = System.getenv("DB_NAME") ?: "snake"
    val dbPort = System.getenv("DB_PORT")?.toInt() ?: 3306
    val dbUser = System.getenv("DB_USER") ?: "root"
    val dbPassword = System.getenv("DB_PASSWORD") ?: "root"
    val redisHost = System.getenv("REDIS_HOST") ?: "localhost"
    val redisPort = System.getenv("REDIS_PORT")?.toInt() ?: 6379
}