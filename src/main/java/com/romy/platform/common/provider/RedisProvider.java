package com.romy.platform.common.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Slf4j
@Component
@RequiredArgsConstructor
public class RedisProvider {

    private static ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private static RedisTemplate<String, Object> staticRedisTemplate;

    @PostConstruct
    private void init() {
        staticRedisTemplate = this.redisTemplate;
        objectMapper = new ObjectMapper();
    }


    public static String getRedisStringValue(String key) {
        if (StringUtils.isBlank(key)) return "";

        Object value = staticRedisTemplate.opsForValue().get(key);

        return value != null ? value.toString() : "";
    }

    public static <T> T getRedisValue(String key, TypeReference<T> typeRef) {
        if (StringUtils.isBlank(key)) return null;

        Object value = staticRedisTemplate.opsForValue().get(key);
        if (value == null) return null;

        if (value instanceof String strValue) {
            try {
                return objectMapper.readValue(strValue, typeRef);
            } catch (JsonProcessingException e) {
                log.debug(e.getMessage());
                throw new RuntimeException("Failed to deserialize Redis value", e);
            }
        }

        try {
            return objectMapper.convertValue(value, typeRef);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unexpected value type in Redis: " + value.getClass(), e);
        }
    }

    /**
     * 캐시 셋팅 (defulat : 1시간)
     */
    public static void setRedisValue(String key, Object value, Long ttl) {
        if (StringUtils.isBlank(key) || value == null) return;

        if (ttl == null) ttl = 60L;

        try {
            String json = objectMapper.writeValueAsString(value);
            staticRedisTemplate.opsForValue().set(key, json);
            staticRedisTemplate.expire(key, ttl, TimeUnit.MINUTES);

        } catch (JsonProcessingException e) {
            log.info("Redis 저장 실패: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize value for Redis", e);
        }
    }

    public static void removeRedisKey(String key) {
        if (StringUtils.isBlank(key)) return;

        staticRedisTemplate.delete(key);
    }
}
