package com.hendisantika.redispipeline.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPipelineService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Save multiple key-value pairs using Redis Pipeline.
     * Pipeline allows sending multiple commands without waiting for individual responses,
     * significantly improving performance for bulk operations.
     *
     * @param data Map of key-value pairs to save
     * @return Number of keys saved
     */
    public long saveMultipleWithPipeline(Map<String, String> data) {
        long startTime = System.currentTimeMillis();
        log.info("Saving {} keys using Redis Pipeline", data.size());

        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            data.forEach((key, value) -> {
                connection.stringCommands().set(key.getBytes(), value.getBytes());
            });
            return null;
        });

        long endTime = System.currentTimeMillis();
        log.info("Pipeline execution completed in {} ms", (endTime - startTime));
        return data.size();
    }

    /**
     * Save multiple key-value pairs WITHOUT using Redis Pipeline (for comparison).
     * Each SET command waits for a response before sending the next one.
     *
     * @param data Map of key-value pairs to save
     * @return Number of keys saved
     */
    public long saveMultipleWithoutPipeline(Map<String, String> data) {
        long startTime = System.currentTimeMillis();
        log.info("Saving {} keys WITHOUT Redis Pipeline", data.size());

        data.forEach((key, value) -> {
            redisTemplate.opsForValue().set(key, value);
        });

        long endTime = System.currentTimeMillis();
        log.info("Non-pipeline execution completed in {} ms", (endTime - startTime));
        return data.size();
    }

    /**
     * Retrieve multiple values using Redis Pipeline.
     *
     * @param keys List of keys to retrieve
     * @return Map of key-value pairs
     */
    public Map<String, String> getMultipleWithPipeline(List<String> keys) {
        long startTime = System.currentTimeMillis();
        log.info("Retrieving {} keys using Redis Pipeline", keys.size());

        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            keys.forEach(key -> {
                connection.stringCommands().get(key.getBytes());
            });
            return null;
        });

        Map<String, String> resultMap = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            Object value = results.get(i);
            if (value != null) {
                // RedisTemplate already deserializes to String with the configured serializer
                if (value instanceof String) {
                    resultMap.put(keys.get(i), (String) value);
                } else if (value instanceof byte[]) {
                    resultMap.put(keys.get(i), new String((byte[]) value));
                }
            }
        }

        long endTime = System.currentTimeMillis();
        log.info("Pipeline retrieval completed in {} ms", (endTime - startTime));
        return resultMap;
    }

    /**
     * Increment multiple counters using Redis Pipeline.
     * Useful for tracking metrics, page views, etc.
     *
     * @param counterKeys List of counter keys to increment
     * @return List of new counter values
     */
    public List<Long> incrementCounters(List<String> counterKeys) {
        log.info("Incrementing {} counters using Redis Pipeline", counterKeys.size());

        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            counterKeys.forEach(key -> {
                connection.stringCommands().incr(key.getBytes());
            });
            return null;
        });

        List<Long> counterValues = new ArrayList<>();
        results.forEach(result -> {
            if (result != null) {
                counterValues.add((Long) result);
            }
        });

        log.info("Counters incremented successfully");
        return counterValues;
    }

    /**
     * Delete multiple keys using Redis Pipeline.
     *
     * @param keys List of keys to delete
     * @return Number of keys deleted
     */
    public long deleteMultiple(List<String> keys) {
        log.info("Deleting {} keys using Redis Pipeline", keys.size());

        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            keys.forEach(key -> {
                connection.keyCommands().del(key.getBytes());
            });
            return null;
        });

        return results.stream().filter(result -> result != null && (Long) result > 0).count();
    }

    /**
     * Check if multiple keys exist using Redis Pipeline.
     *
     * @param keys List of keys to check
     * @return Map of key existence status
     */
    public Map<String, Boolean> checkKeysExist(List<String> keys) {
        log.info("Checking existence of {} keys using Redis Pipeline", keys.size());

        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            keys.forEach(key -> {
                connection.keyCommands().exists(key.getBytes());
            });
            return null;
        });

        Map<String, Boolean> existenceMap = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            Object result = results.get(i);
            boolean exists = false;
            if (result instanceof Boolean) {
                exists = (Boolean) result;
            } else if (result instanceof Long) {
                exists = ((Long) result) > 0;
            }
            existenceMap.put(keys.get(i), exists);
        }

        return existenceMap;
    }

    /**
     * Perform mixed operations using Redis Pipeline.
     * Demonstrates combining different Redis commands in a single pipeline.
     *
     * @param keysToSet    Map of keys to set
     * @param keysToGet    List of keys to get
     * @param keysToDelete List of keys to delete
     * @return Summary of operations
     */
    public String performMixedOperations(Map<String, String> keysToSet,
                                         List<String> keysToGet,
                                         List<String> keysToDelete) {
        log.info("Performing mixed operations: SET={}, GET={}, DELETE={}",
                keysToSet.size(), keysToGet.size(), keysToDelete.size());

        long startTime = System.currentTimeMillis();

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            // SET operations
            keysToSet.forEach((key, value) -> {
                connection.stringCommands().set(key.getBytes(), value.getBytes());
            });

            // GET operations
            keysToGet.forEach(key -> {
                connection.stringCommands().get(key.getBytes());
            });

            // DELETE operations
            keysToDelete.forEach(key -> {
                connection.keyCommands().del(key.getBytes());
            });

            return null;
        });

        long endTime = System.currentTimeMillis();
        return String.format("Mixed operations completed in %d ms", (endTime - startTime));
    }
}
