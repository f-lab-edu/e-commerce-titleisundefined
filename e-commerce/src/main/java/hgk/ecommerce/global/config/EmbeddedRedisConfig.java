package hgk.ecommerce.global.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;

@Configuration
@Profile("test")
@EnableCaching

public class EmbeddedRedisConfig {
    private RedisServer redisServer;
    private final int redisPort = 6379;
    private final String redisHost = "localhost";


    

//    @PostConstruct
//    private void postConstruct() throws IOException {
//        int port = isRedisRunning()? findAvailablePort() : redisPort;
//        redisServer = new RedisServer(port);
//        redisServer.start();
//    }
//
//    @PreDestroy
//    private void preDestroy() {
//        if (redisServer != null)
//            redisServer.stop();
//    }
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory(redisHost, redisPort);
//    }
//
//    @Bean
//    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
//        RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder
//                .fromConnectionFactory(redisConnectionFactory)
//                .cacheDefaults(redisCacheConfiguration()).build();
//        return redisCacheManager;
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory());
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//
//        return redisTemplate;
//    }
//
//    private RedisCacheConfiguration redisCacheConfiguration() {
//        return RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofSeconds(300))
//                .disableCachingNullValues()
//                .serializeKeysWith(
//                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
//                )
//                .serializeValuesWith(
//                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
//                );
//    }
//
//    private boolean isRedisRunning() throws IOException {
//        return isRunning(executeGrepProcessCommand(redisPort));
//    }
//
//    /**
//     * 현재 PC/서버에서 사용가능한 포트 조회
//     */
//    public int findAvailablePort() throws IOException {
//
//        for (int port = 10000; port <= 65535; port++) {
//            Process process = executeGrepProcessCommand(port);
//            if (!isRunning(process)) {
//                return port;
//            }
//        }
//
//        throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
//    }
//
//    /**
//     * 해당 port를 사용중인 프로세스 확인하는 sh 실행
//     */
//    private Process executeGrepProcessCommand(int port) throws IOException {
//        String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
//        String[] shell = {"/bin/sh", "-c", command};
//        return Runtime.getRuntime().exec(shell);
//    }
//
//    /**
//     * 해당 Process가 현재 실행중인지 확인
//     */
//    private boolean isRunning(Process process) {
//        String line;
//        StringBuilder pidInfo = new StringBuilder();
//
//        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//
//            while ((line = input.readLine()) != null) {
//                pidInfo.append(line);
//            }
//
//        } catch (Exception e) {
//        }
//
//        return !StringUtils.isEmpty(pidInfo.toString());
//    }
}
