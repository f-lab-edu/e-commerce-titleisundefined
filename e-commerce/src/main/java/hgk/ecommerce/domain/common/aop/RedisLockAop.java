package hgk.ecommerce.domain.common.aop;

import hgk.ecommerce.domain.common.annotation.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisLockAop {
    private static final String REDISSON_KEY_PREFIX = "RLOCK_";

    private final RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(hgk.ecommerce.domain.common.annotation.RedisLock)")
    public Object spinLock(final ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisLock distributeLock = method.getAnnotation(RedisLock.class);


    }

}
