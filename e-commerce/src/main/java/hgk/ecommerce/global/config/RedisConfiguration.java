package hgk.ecommerce.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//@Configuration
//@PropertySource("classpath:application-redis.properties")
public class RedisConfiguration {

    @Value("${spring.session.redis.host}")
    private String redisHost;

    @Value("${spring.session.redis.port}")
    private int redisPort;

    @Value("${spring.session.redis.password}")
    private String sessionRedisPassword;

    @Value("${cache.redis.host}")
    private String cacheRedisHost;

    @Value("${cache.redis.port}")
    private int cacheRedisPort;

    @Value("${cache.redis.password}")
    private String cacheRedisPassword;


    @Primary
    @Bean(name = "redisSessionFactory")
    public RedisConnectionFactory redisSessionConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPort(redisPort);
        redisStandaloneConfiguration.setPassword(sessionRedisPassword);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(
                redisStandaloneConfiguration);

        return lettuceConnectionFactory;
    }

    @Bean(name = "redisCacheFactory")
    public RedisConnectionFactory redisCacheConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(cacheRedisHost);
        redisStandaloneConfiguration.setPort(cacheRedisPort);
        redisStandaloneConfiguration.setPassword(cacheRedisPassword);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(
                redisStandaloneConfiguration);

        return lettuceConnectionFactory;
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
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisCacheConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

}
