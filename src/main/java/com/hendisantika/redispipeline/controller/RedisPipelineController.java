package com.hendisantika.redispipeline.controller;

import com.hendisantika.redispipeline.service.RedisPipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-redis-pipeline
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 10/13/25
 * Time: 18:35
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisPipelineController {

    private final RedisPipelineService redisPipelineService;

    /**
     * Save multiple key-value pairs using Redis Pipeline.
     * Example: POST /api/redis/pipeline/save
     * Body: {"key1": "value1", "key2": "value2", "key3": "value3"}
     */
    @PostMapping("/pipeline/save")
    public ResponseEntity<Map<String, Object>> saveWithPipeline(@RequestBody Map<String, String> data) {
        log.info("Received request to save {} keys with pipeline", data.size());
        long count = redisPipelineService.saveMultipleWithPipeline(data);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("keysProcessed", count);
        response.put("message", "Data saved successfully using Redis Pipeline");

        return ResponseEntity.ok(response);
    }

    /**
     * Save multiple key-value pairs WITHOUT using Redis Pipeline (for performance comparison).
     * Example: POST /api/redis/normal/save
     * Body: {"key1": "value1", "key2": "value2", "key3": "value3"}
     */
    @PostMapping("/normal/save")
    public ResponseEntity<Map<String, Object>> saveWithoutPipeline(@RequestBody Map<String, String> data) {
        log.info("Received request to save {} keys without pipeline", data.size());
        long count = redisPipelineService.saveMultipleWithoutPipeline(data);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("keysProcessed", count);
        response.put("message", "Data saved successfully without Redis Pipeline");

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve multiple values using Redis Pipeline.
     * Example: GET /api/redis/pipeline/get?keys=key1,key2,key3
     */
    @GetMapping("/pipeline/get")
    public ResponseEntity<Map<String, Object>> getWithPipeline(@RequestParam List<String> keys) {
        log.info("Received request to get {} keys with pipeline", keys.size());
        Map<String, String> data = redisPipelineService.getMultipleWithPipeline(keys);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", data.size());
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    /**
     * Increment multiple counters using Redis Pipeline.
     * Example: POST /api/redis/pipeline/increment
     * Body: ["counter1", "counter2", "counter3"]
     */
    @PostMapping("/pipeline/increment")
    public ResponseEntity<Map<String, Object>> incrementCounters(@RequestBody List<String> counterKeys) {
        log.info("Received request to increment {} counters", counterKeys.size());
        List<Long> values = redisPipelineService.incrementCounters(counterKeys);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("counters", counterKeys);
        response.put("values", values);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete multiple keys using Redis Pipeline.
     * Example: DELETE /api/redis/pipeline/delete
     * Body: ["key1", "key2", "key3"]
     */
    @DeleteMapping("/pipeline/delete")
    public ResponseEntity<Map<String, Object>> deleteKeys(@RequestBody List<String> keys) {
        log.info("Received request to delete {} keys", keys.size());
        long deletedCount = redisPipelineService.deleteMultiple(keys);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("deletedCount", deletedCount);
        response.put("message", "Keys deleted successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Check if multiple keys exist using Redis Pipeline.
     * Example: GET /api/redis/pipeline/exists?keys=key1,key2,key3
     */
    @GetMapping("/pipeline/exists")
    public ResponseEntity<Map<String, Object>> checkKeysExist(@RequestParam List<String> keys) {
        log.info("Received request to check existence of {} keys", keys.size());
        Map<String, Boolean> existenceMap = redisPipelineService.checkKeysExist(keys);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("results", existenceMap);

        return ResponseEntity.ok(response);
    }

    /**
     * Generate sample data for testing.
     * Example: POST /api/redis/test/generate?count=100
     */
    @PostMapping("/test/generate")
    public ResponseEntity<Map<String, Object>> generateTestData(@RequestParam(defaultValue = "10") int count) {
        log.info("Generating {} test records", count);

        Map<String, String> testData = new HashMap<>();
        for (int i = 1; i <= count; i++) {
            testData.put("test:user:" + i, "User " + i);
        }

        long savedCount = redisPipelineService.saveMultipleWithPipeline(testData);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("generated", savedCount);
        response.put("message", "Test data generated successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Performance comparison endpoint.
     * Compares pipeline vs non-pipeline performance.
     * Example: POST /api/redis/test/compare?count=100
     */
    @PostMapping("/test/compare")
    public ResponseEntity<Map<String, Object>> comparePerformance(@RequestParam(defaultValue = "100") int count) {
        log.info("Running performance comparison with {} operations", count);

        // Generate test data
        Map<String, String> testData = new HashMap<>();
        for (int i = 1; i <= count; i++) {
            testData.put("perf:test:" + i, "Value " + i);
        }

        // Test with pipeline
        long startPipeline = System.currentTimeMillis();
        redisPipelineService.saveMultipleWithPipeline(testData);
        long pipelineTime = System.currentTimeMillis() - startPipeline;

        // Generate new test data for fair comparison
        Map<String, String> testData2 = new HashMap<>();
        for (int i = 1; i <= count; i++) {
            testData2.put("perf:test2:" + i, "Value " + i);
        }

        // Test without pipeline
        long startNormal = System.currentTimeMillis();
        redisPipelineService.saveMultipleWithoutPipeline(testData2);
        long normalTime = System.currentTimeMillis() - startNormal;

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("operations", count);
        response.put("pipelineTime", pipelineTime + " ms");
        response.put("normalTime", normalTime + " ms");
        response.put("speedup", String.format("%.2fx faster", (double) normalTime / pipelineTime));
        response.put("message", "Pipeline is significantly faster for bulk operations");

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Redis Pipeline Service");
        return ResponseEntity.ok(response);
    }
}
