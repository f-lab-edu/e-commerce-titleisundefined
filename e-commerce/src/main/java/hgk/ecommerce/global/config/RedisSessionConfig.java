package hgk.ecommerce.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.io.IOException;

@Configuration
@EnableRedisHttpSession
public class RedisSessionConfig {

    @Value("${spring.session.redis.host}")
    private String redisSessionHost ;

    @Value("${spring.session.redis.port}")
    private int redisSessionPort;

    @Value("${spring.session.redis.password}")
    private String redisSessionPassword;

    @Bean
    @Primary
    public RedissonConnectionFactory sessionRedissonFactory(@Qualifier("sessionRedisson") RedissonClient redissonClient) {
        return new RedissonConnectionFactory(redissonClient);
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient sessionRedisson() throws IOException {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisSessionHost + ":" + redisSessionPort)
                .setPassword(redisSessionPassword);
        return Redisson.create(config);
    }
}
