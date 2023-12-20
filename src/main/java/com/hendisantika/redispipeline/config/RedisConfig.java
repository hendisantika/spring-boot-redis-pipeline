package com.hendisantika.redispipeline.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-redis-pipeline
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 12/20/23
 * Time: 07:52
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class RedisConfig {
    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName("localhost");
        jedisConnectionFactory.setPort(6379);
//        jedisConnectionFactory.setPassword("<server-password-here>");
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }
}
