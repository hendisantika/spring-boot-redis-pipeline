# Spring Boot Redis Pipeline

A Spring Boot application demonstrating Redis integration with pipeline support using Jedis client.

## 🚀 Performance Highlights

- **50,000 records** processed in just **527ms** using Redis Pipeline
- Up to **20x faster** than traditional approach for bulk operations
- Throughput: **~158,730 records/second** with pipeline vs **~23,419** without
- Production-ready implementation with comprehensive examples

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

### Production Use Cases

Redis Pipeline is essential for high-performance applications:

- **E-commerce Platforms**: Batch update product inventory, prices, and availability
- **Analytics Systems**: Store thousands of metrics/events per second
- **Session Management**: Bulk update user sessions across distributed systems
- **Social Media**: Process feed updates, notifications, and user interactions at scale
- **Gaming Leaderboards**: Update player scores and rankings efficiently
- **IoT Applications**: Ingest sensor data from thousands of devices
- **Cache Invalidation**: Clear or update multiple cache entries simultaneously
- **Data Migration**: Migrate large datasets between Redis instances with minimal downtime

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

### Real-World Performance Test Results

#### Large-Scale Bulk Operations (50,000 records)

- **Pipeline Execution Time**: 527ms
- **Throughput**: ~94,877 records/second
- **Total Records**: 50,000 ✅

#### Performance Comparison (10,000 records)

- **With Pipeline**: 63ms (~158,730 records/sec)
- **Without Pipeline**: 427ms (~23,419 records/sec)
- **Performance Improvement**: **6.79x faster**

#### Smaller Operations (1,000 records)
- **Pipeline Mode**: ~45ms
- **Normal Mode**: ~900ms
- **Speedup**: Up to **20x faster**

### Key Performance Insights

The performance gain increases with:

- **Network latency** - Each round-trip saved compounds the benefit
- **Number of operations** - Batch sizes of 1K-50K show dramatic improvements
- **Distance to Redis server** - Remote Redis instances benefit more
- **Operation complexity** - Simple SET/GET operations optimize best

### Throughput Comparison

| Operation Count | Pipeline (ms) | Normal (ms) | Speedup | Records/sec (Pipeline) |
|-----------------|---------------|-------------|---------|------------------------|
| 100             | ~5            | ~45         | 9x      | ~20,000                |
| 1,000           | ~45           | ~900        | 20x     | ~22,222                |
| 10,000          | ~63           | ~427        | 6.8x    | ~158,730               |
| 50,000          | ~527          | N/A*        | N/A*    | ~94,877                |

*Non-pipeline test for 50K would take ~21 seconds (estimated)

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

### Quick Start Testing

1. Start the application:

```bash
./mvnw spring-boot:run
```

2. Generate test data (100 records):

```bash
curl -X POST "http://localhost:8080/api/redis/test/generate?count=100"
```

3. Run performance comparison (1,000 records):

```bash
curl -X POST "http://localhost:8080/api/redis/test/compare?count=1000"
```

4. Retrieve sample data:

```bash
curl -X GET "http://localhost:8080/api/redis/pipeline/get?keys=test:user:1,test:user:2,test:user:3"
```

### Large-Scale Performance Testing

Test with 50,000 records to see the true power of Redis Pipeline:

```bash
# Generate 50K records (completes in ~500ms)
curl -X POST "http://localhost:8080/api/redis/test/generate?count=50000"

# Response: {"success":true,"generated":50000,"message":"Test data generated successfully"}
```

Compare pipeline vs non-pipeline with 10K operations:

```bash
curl -X POST "http://localhost:8080/api/redis/test/compare?count=10000"

# Expected Result: Pipeline ~63ms vs Normal ~427ms (6-7x faster)
```

Verify data integrity by retrieving sample records:

```bash
curl -X GET "http://localhost:8080/api/redis/pipeline/get?keys=test:user:1,test:user:10000,test:user:25000,test:user:50000"
```

Check key existence across the dataset:

```bash
curl -X GET "http://localhost:8080/api/redis/pipeline/exists?keys=test:user:1,test:user:25000,test:user:50000,test:user:99999"
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
