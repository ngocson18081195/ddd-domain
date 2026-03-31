package com.xxx.ddd.infrastructure.distributed.redisson.impl;

import com.xxx.ddd.infrastructure.distributed.redisson.RedisDistributedLocker;
import com.xxx.ddd.infrastructure.distributed.redisson.RedisDistributedService;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisDistributedLockImpl implements RedisDistributedService {
    private static final Logger log = LoggerFactory.getLogger(RedisDistributedLockImpl.class);
    @Resource
    private RedissonClient redissonClient;

    @Override
    public RedisDistributedLocker getDistributedLock(String lockKey) {
        RLock rLock = redissonClient.getLock(lockKey);

        return new RedisDistributedLocker() {
            @Override
            public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
                boolean isLockSuccess = rLock.tryLock(waitTime, leaseTime, unit);
                log.info("{} get result: {}", lockKey, isLockSuccess);
                return isLockSuccess;
            }

            @Override
            public void lock(long leaseTime, TimeUnit unit) {
                rLock.lock(leaseTime, unit);
            }

            @Override
            public void unlock() {
                if (isLocked() && isHeldBuCurrentThread()) {
                    rLock.unlock();
                }
            }

            @Override
            public boolean isLocked() {
                return rLock.isLocked();
            }

            @Override
            public boolean isHeldByThread(long threadId) {
                return rLock.isHeldByThread(threadId);
            }

            @Override
            public boolean isHeldBuCurrentThread() {
                return rLock.isHeldByCurrentThread();
            }
        };
    }
}
