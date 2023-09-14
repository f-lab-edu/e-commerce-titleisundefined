package hgk.ecommerce.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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

}
