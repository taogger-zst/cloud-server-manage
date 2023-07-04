package com.taogger.gateway.service.lock;

import com.taogger.gateway.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁redis加工
 * @author chennz
 * @date 2021/9/8 9:50
 */
@Service
@RequiredArgsConstructor
public class RedissonDistributedLocker implements DistributedLocker {
    private final RedissonClient redissonClient;
    private final RedisService redisService;

    @Override
    public RLock get(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    @Override
    public RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    @Override
    public RLock lock(String lockKey, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    @Override
    public RLock lock(String lockKey, TimeUnit unit ,int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, unit);
        return lock;
    }

    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }

    @Override
    public void unlock(RLock lock) {
        lock.unlock();
    }

    @Override
    public boolean tryFairLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
        RLock fairLock = redissonClient.getFairLock(lockKey);
        try {
            boolean existKey = existKey(lockKey);
            boolean tryLock = fairLock.tryLock(waitTime, leaseTime, unit);
            // 防止同一个线程重复请求，线程可以重入。已经存在了，就直接返回
            if (tryLock && existKey) {
                return false;
            }
            return tryLock;
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean existKey(String key) {
        return redisService.hasKey(key);
    }

}
