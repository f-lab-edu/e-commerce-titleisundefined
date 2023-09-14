package hgk.ecommerce.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("release")
public class RedisConfiguration {
    @Value("${spring.session.redis.host}")
    private String redisSessionHost;

    @Value("${spring.session.redis.port}")
    private int redisSessionPort;

    @Value("${spring.session.redis.password}")
    private String redisSessionPassword;

    @Value("${spring.cache.redis.host}")
    private String redisCacheHost;

    @Value("${spring.cache.redis.port}")
    private int redisCachePort;

    @Value("${spring.cache.redis.password}")
    private String redisCachePassword;

    @Primary
    @Bean(name = "redisSessionFactory")
    public RedisConnectionFactory redisSessionConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisSessionHost);
        redisConfig.setPort(redisSessionPort);
        redisConfig.setPassword(redisSessionPassword);

        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public StringRedisTemplate sessionTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisSessionConnectionFactory());
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(new ObjectMapper()));
        stringRedisTemplate.setDefaultSerializer(new StringRedisSerializer());
        stringRedisTemplate.afterPropertiesSet();

        return stringRedisTemplate;
    }

    @Bean
    public RedisConnectionFactory redisCacheConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisCacheHost);
        redisStandaloneConfiguration.setPort(redisCachePort);
        redisStandaloneConfiguration.setPassword(redisCachePassword);

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisCacheConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    @Bean
    public CacheManager redisCacheManager(@Qualifier("redisCacheConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
                RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration()).build();
        return redisCacheManager;
    }

    private RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                );
    }
}
