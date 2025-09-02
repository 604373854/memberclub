/**
 * @(#)RedisConfiguration.java, 一月 31, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;

/**
 * 当应用配置为使用 Redis 作为缓存或用户标签存储时，提供 Redis 与 Redisson 的 Bean 定义。
 */
@Configuration
@ConditionalOnProperty(name = "memberclub.infrastructure.cache", havingValue = "redis")
public class RedisConfiguration {


    @ConditionalOnProperty(name = "memberclub.infrastructure.cache", havingValue = "redis")
    @Bean
    public RedissonClient redisson() {
        Config config = new Config();
        // 配置单节点 Redis 服务地址；集群环境需调整。
        config.useSingleServer().setAddress("redis://localhost:6379");
        config.setCodec(new CompositeCodec(new StringCodec(), new JsonJacksonCodec()));
        return Redisson.create(config);
    }


    /**
     * 配置使用 JSON 序列化的 {@link RedisTemplate}。
     *
     * @param factory 连接工厂
     * @return 配置完成的模板实例
     */
    @Bean
    @ConditionalOnProperty(name = "memberclub.infrastructure.usertag", havingValue = "redis")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂及值序列化器。
        template.setConnectionFactory(factory);

        // 使用 Jackson2JsonRedisSerializer 替代 JDK 序列化。
        Jackson2JsonRedisSerializer jacksonSeial = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper om = new ObjectMapper();
        // 设置所有字段可见以便序列化。
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 允许非 final 类被序列化；如 String 这种 final 类使用自身的编码。
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(FAIL_ON_EMPTY_BEANS, false);
        om.disable(FAIL_ON_EMPTY_BEANS);
        om.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jacksonSeial.setObjectMapper(om);

        // key 使用字符串序列化，value 使用 JSON 序列化。
        template.setValueSerializer(jacksonSeial);
        template.setKeySerializer(new StringRedisSerializer());

        // Hash 的 key 与 value 也遵循同样的序列化规则。
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jacksonSeial);
        template.afterPropertiesSet();

        return template;
    }

    /**
     * 暴露 Redis Hash 结构的操作。
     *
     * @param redisTemplate 配置好的模板
     * @return Hash 操作对象
     */
    @Bean
    @ConditionalOnProperty(name = "memberclub.infrastructure.usertag", havingValue = "redis")
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * 暴露 Redis String 类型的操作。
     *
     * @param redisTemplate 配置好的模板
     * @return Value 操作对象
     */
    @Bean
    @ConditionalOnProperty(name = "memberclub.infrastructure.usertag", havingValue = "redis")
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * 暴露 Redis List 结构的操作。
     *
     * @param redisTemplate 配置好的模板
     * @return List 操作对象
     */
    @Bean
    @ConditionalOnProperty(name = "memberclub.infrastructure.usertag", havingValue = "redis")
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * 暴露 Redis Set 结构的操作。
     *
     * @param redisTemplate 配置好的模板
     * @return Set 操作对象
     */
    @Bean
    @ConditionalOnProperty(name = "memberclub.infrastructure.usertag", havingValue = "redis")
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * 暴露 Redis Sorted Set 结构的操作。
     *
     * @param redisTemplate 配置好的模板
     * @return ZSet 操作对象
     */
    @Bean
    @ConditionalOnProperty(name = "memberclub.infrastructure.usertag", havingValue = "redis")
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}
