# Snake game backend

### What it does
- Saves user high-scores
- Retrieves the best 10 of all time

### Additional features
- Rate limiting by IP
- cache
- validation

### Environment variables (name, default value):
- **DB_HOST**: `localhost`
- **DB_NAME**: `snake`
- **DB_PORT**: `3306`
- **DB_USER**: `root`
- **DB_PASSWORD**: `root`
- **REDIS_HOST**: `localhost`
- **REDIS_PORT**: `6379`

### Tools used:
- Ktor for http server with routing
- Ktorm as database ORM
- MySQL database
- Gson as json serializer
- Redis with Jedis
- Docker container
- (TODO) Kubernetes deploy