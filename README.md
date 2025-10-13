# Spring Boot Redis Pipeline

A Spring Boot application demonstrating Redis integration with pipeline support using Jedis client.

## Prerequisites

- Java 21 or higher
- Maven 3.x
- Docker and Docker Compose

## Tech Stack

- Spring Boot 3.5.6
- Spring Data Redis
- Jedis 7.0.0
- Redis 7 (Alpine)
- Docker Compose Integration

## Project Structure

```
spring-boot-redis-pipeline/
├── src/
│   ├── main/
│   │   ├── java/com/hendisantika/redispipeline/
│   │   │   ├── SpringBootRedisPipelineApplication.java
│   │   │   ├── config/
│   │   │   │   └── RedisConfig.java
│   │   │   ├── controller/
│   │   │   │   └── RedisPipelineController.java
│   │   │   └── service/
│   │   │       └── RedisPipelineService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── docker-compose.yml
└── pom.xml
```

## Configuration

The application is configured with the following Redis settings in `application.properties`:

```properties
# Spring Boot Docker Compose integration
spring.docker.compose.enabled=true
spring.docker.compose.file=docker-compose.yml
# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=60000
# Application Configuration
server.port=8080
```

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd spring-boot-redis-pipeline
```

### 2. Start Redis with Docker Compose

The application uses Spring Boot Docker Compose integration, which automatically manages the Redis container. However,
you can also manually start Redis:

```bash
docker-compose up -d
```

This will start a Redis 7 Alpine container on port 6379 with data persistence enabled.

### 3. Verify Redis is Running

```bash
docker ps
```

You should see a container named `redis-server` running.

### 4. Build the Application

```bash
./mvnw clean install
```

### 5. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on port 8080.

## Docker Compose Configuration

The `docker-compose.yml` file includes:

- Redis 7 Alpine image
- Port mapping: 6379:6379
- Data persistence with named volume
- Automatic restart policy

## Redis Configuration

The application uses `JedisConnectionFactory` to connect to Redis with the following configuration:

- Host: localhost
- Port: 6379
- Timeout: 60000ms
- Default serializer: String serializer

See `RedisConfig.java` for the complete configuration.

## Development

The application includes Spring Boot DevTools for hot reload during development.

### Maven Wrapper

This project includes Maven Wrapper, so you don't need to have Maven installed:

```bash
# On Unix/Mac
./mvnw clean install
./mvnw spring-boot:run

# On Windows
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

## Stopping the Application

1. Stop the Spring Boot application (Ctrl+C)
2. Stop the Redis container:

```bash
docker-compose down
```

To also remove the data volume:

```bash
docker-compose down -v
```

## Dependencies

Key dependencies include:

- `spring-boot-starter-web` - Web application support
- `spring-boot-starter-data-redis` - Redis integration
- `jedis` - Redis Java client
- `spring-boot-docker-compose` - Docker Compose integration
- `spring-boot-devtools` - Development tools
- `lombok` - Reduce boilerplate code

## Redis Pipeline

Redis pipelining is a technique to improve performance by sending multiple commands to the server without waiting for
replies, and then reading the replies in a single step. This project is configured to support Redis pipeline operations
through the Jedis client.

### What is Redis Pipeline?

In typical Redis operations, each command waits for a response before sending the next one. This round-trip time (RTT)
can significantly impact performance when executing multiple commands.

**Without Pipeline:**

```
Client: SET key1 value1
Server: OK
Client: SET key2 value2
Server: OK
Client: SET key3 value3
Server: OK
Total time: 3 × RTT
```

**With Pipeline:**

```
Client: SET key1 value1
Client: SET key2 value2
Client: SET key3 value3
Server: OK
Server: OK
Server: OK
Total time: 1 × RTT
```

### Use Cases

This project demonstrates practical Redis Pipeline implementations:

1. **Bulk Data Operations** - Save/retrieve multiple key-value pairs efficiently
2. **Counter Management** - Increment multiple counters simultaneously
3. **Cache Warming** - Populate cache with multiple entries quickly
4. **Batch Processing** - Process multiple operations in a single network call
5. **Performance Optimization** - Reduce network overhead for bulk operations

## API Endpoints

The application provides the following REST endpoints to demonstrate Redis Pipeline usage:

### 1. Save Data with Pipeline

```bash
curl -X POST http://localhost:8080/api/redis/pipeline/save \
  -H "Content-Type: application/json" \
  -d '{
    "user:1": "John Doe",
    "user:2": "Jane Smith",
    "user:3": "Bob Johnson"
  }'
```

**Response:**

```json
{
  "success": true,
  "keysProcessed": 3,
  "message": "Data saved successfully using Redis Pipeline"
}
```

### 2. Save Data Without Pipeline (for comparison)

```bash
curl -X POST http://localhost:8080/api/redis/normal/save \
  -H "Content-Type: application/json" \
  -d '{
    "product:1": "Laptop",
    "product:2": "Mouse",
    "product:3": "Keyboard"
  }'
```

### 3. Retrieve Multiple Values

```bash
curl -X GET "http://localhost:8080/api/redis/pipeline/get?keys=user:1,user:2,user:3"
```

**Response:**

```json
{
  "success": true,
  "count": 3,
  "data": {
    "user:1": "John Doe",
    "user:2": "Jane Smith",
    "user:3": "Bob Johnson"
  }
}
```

### 4. Increment Counters

```bash
curl -X POST http://localhost:8080/api/redis/pipeline/increment \
  -H "Content-Type: application/json" \
  -d '["page:views:home", "page:views:about", "page:views:contact"]'
```

**Response:**

```json
{
  "success": true,
  "counters": ["page:views:home", "page:views:about", "page:views:contact"],
  "values": [1, 1, 1]
}
```

### 5. Check Key Existence

```bash
curl -X GET "http://localhost:8080/api/redis/pipeline/exists?keys=user:1,user:2,user:999"
```

**Response:**

```json
{
  "success": true,
  "results": {
    "user:1": true,
    "user:2": true,
    "user:999": false
  }
}
```

### 6. Delete Multiple Keys

```bash
curl -X DELETE http://localhost:8080/api/redis/pipeline/delete \
  -H "Content-Type: application/json" \
  -d '["user:1", "user:2", "user:3"]'
```

### 7. Generate Test Data

```bash
curl -X POST "http://localhost:8080/api/redis/test/generate?count=100"
```

This generates 100 test records to demonstrate pipeline performance.

### 8. Performance Comparison

```bash
curl -X POST "http://localhost:8080/api/redis/test/compare?count=1000"
```

**Response:**

```json
{
  "success": true,
  "operations": 1000,
  "pipelineTime": "45 ms",
  "normalTime": "892 ms",
  "speedup": "19.82x faster",
  "message": "Pipeline is significantly faster for bulk operations"
}
```

This endpoint compares the performance of pipeline vs non-pipeline operations, typically showing 10-20x performance
improvement.

### 9. Health Check

```bash
curl http://localhost:8080/api/redis/health
```

## Performance Benefits

Based on testing with 1000 operations:

- **Pipeline Mode**: ~45ms
- **Normal Mode**: ~900ms
- **Speedup**: Up to 20x faster

The performance gain increases with:

- Network latency
- Number of operations
- Distance to Redis server

## Code Examples

### Using Pipeline in Java

```java

@Service
public class RedisPipelineService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void saveMultiple(Map<String, String> data) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            data.forEach((key, value) -> {
                connection.stringCommands().set(key.getBytes(), value.getBytes());
            });
            return null;
        });
    }
}
```

### Key Implementation Details

1. **RedisPipelineService** (`src/main/java/com/hendisantika/redispipeline/service/RedisPipelineService.java`)
    - Implements various pipeline operations
    - Provides performance comparison methods
    - Includes logging for monitoring

2. **RedisPipelineController** (`src/main/java/com/hendisantika/redispipeline/controller/RedisPipelineController.java`)
    - REST API endpoints
    - Request/response handling
    - Test data generation

3. **RedisConfig** (`src/main/java/com/hendisantika/redispipeline/config/RedisConfig.java`)
    - Jedis connection factory
    - RedisTemplate configuration
    - Serialization settings

## Testing the Application

1. Start the application:

```bash
./mvnw spring-boot:run
```

2. Generate test data:

```bash
curl -X POST "http://localhost:8080/api/redis/test/generate?count=100"
```

3. Run performance comparison:

```bash
curl -X POST "http://localhost:8080/api/redis/test/compare?count=1000"
```

4. Retrieve data:

```bash
curl -X GET "http://localhost:8080/api/redis/pipeline/get?keys=test:user:1,test:user:2,test:user:3"
```

## When to Use Redis Pipeline

**Use Pipeline when:**

- Performing multiple independent operations
- Bulk loading data
- Batch processing
- Network latency is significant
- You need maximum throughput

**Don't use Pipeline when:**

- Operations depend on previous results
- You need immediate response for each command
- Working with Redis transactions (use MULTI/EXEC instead)
- Single operation only

## Author

hendisantika

- Email: hendisantika@gmail.com
- Telegram: @hendisantika34

## License

This project is created for demonstration purposes.
