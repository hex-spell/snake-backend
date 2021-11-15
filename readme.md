# Snake game high-scores
Learning Kotlin applications for backend

## What it does
- Saves user high-scores
- Retrieves the best 10 of all time

## Http api live demo 
### http://modularizar.com/snake-backend

### Example request:
``` shell
curl --request GET \
  --url http://modularizar.com/snake-backend \
  --header 'Content-Type: application/json'
```
___

## Endpoints
### GET /
- Returns array of `
  {
  "id": Int,
  "username": String,
  "points": Int,
  "saved_at": String
  }
`

### POST /
- Receives `
  {
  "username": String,
  "points": Int
  }
  `
- Returns new entity as `
  {
  "id": Int,
  "username": String,
  "points": Int,
  "saved_at": String
  }
  `

___

### Environment variables (name, default value):
- **HOST**: `localhost`
- **DB_HOST**: `localhost`
- **DB_NAME**: `snake`
- **DB_PORT**: `3306`
- **DB_USER**: `root`
- **DB_PASSWORD**: `root`
- **REDIS_HOST**: `localhost`
- **REDIS_PORT**: `6379`

---

### Additional features
- Rate limiting by IP
- cache
- validation

### Tools used:
- Ktor for http server with routing
- Ktorm as database ORM
- MySQL database
- Gson as json serializer
- Redis with Jedis
- Docker

---

## Hosting
### (Using docker-compose)
``` yaml
services:
  reverse_proxy:
      container_name: reverse_proxy
      image: nginx:1.17.10
      depends_on:
        # {...} my other services
        - snake-backend
      volumes:
        - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      ports:
        - "80:80"
  snake-backend:
      container_name: snake-backend
      build:
        context: ./snake-backend
      user: root
      depends_on:
        - mysql
        - redis
      environment:
        HOST: snake-backend
        DB_HOST: mysql
        DB_NAME: "snake"
        DB_PORT: "3306"
        DB_USER: "root"
        DB_PASSWORD: "agustin"
        REDIS_HOST: redis
        REDIS_PORT: "6379"
      restart: always
  
  mysql:
    image: mysql:8.0.13
    command: --default-authentication-plugin=mysql_native_password
    restart: always
    environment:
      # {...} super secret stuff, yknow
    volumes:
      - ./mysql-data:/var/lib/mysql
      - ./init:/docker-entrypoint-initdb.d
    ports:
      - "3306:3306"

  redis:
    image: "redis:alpine"

    command: redis-server

    ports:
      - "6379:6379"

    volumes:
     - ./redis-data:/var/lib/redis
     - ./redis.conf:/usr/local/etc/redis/redis.conf
```
### Database init script

``` roomsql
CREATE DATABASE IF NOT EXISTS `snake`;

use snake;

CREATE TABLE `players` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`username` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
`points` int(11) NOT NULL,
`saved_at` datetime DEFAULT NOW(),
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
```