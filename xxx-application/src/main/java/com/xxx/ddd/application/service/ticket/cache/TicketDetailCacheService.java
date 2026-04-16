package com.xxx.ddd.application.service.ticket.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xxx.ddd.application.service.model.cache.TicketDetailCache;
import com.xxx.ddd.domain.model.entity.TicketDetail;
import com.xxx.ddd.domain.service.TicketDetailDomainService;
import com.xxx.ddd.infrastructure.cache.redis.RedisInfrasService;
import com.xxx.ddd.infrastructure.distributed.redisson.RedisDistributedLocker;
import com.xxx.ddd.infrastructure.distributed.redisson.RedisDistributedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TicketDetailCacheService {
    @Autowired
    private RedisDistributedService redisDistributedService;
    @Autowired // Khai bao cache
    private RedisInfrasService redisInfrasService;
    @Autowired
    private TicketDetailDomainService ticketDetailDomainService;

    //use guava
    private final static Cache<Long, TicketDetailCache> TICKET_DETAIL_CACHE = CacheBuilder.newBuilder()
            .initialCapacity(10) // khởi tạo 10 item
            .concurrencyLevel(4) // CPU
            .expireAfterWrite(10, TimeUnit.MINUTES).build(); //

    public boolean orderTicketByUser(Long ticketId) {
        TICKET_DETAIL_CACHE.invalidate(ticketId);
        redisInfrasService.delete(genEventItemKey(ticketId));
        return true;
    }


    public TicketDetail getTicketDefaultCacheNormal(Long id, Long version) {
        // 1. get ticket item by redis
        TicketDetail ticketDetail = redisInfrasService.getObject(genEventItemKey(id), TicketDetail.class);
        // 2. YES -> Hit cache
        if (ticketDetail != null) {
            log.info("FROM CACHE {}, {}, {}", id, version, ticketDetail);
            return ticketDetail;
        }
        // 3. If NO --> Missing cache

        // 4. Get data from DBS
        ticketDetail = ticketDetailDomainService.getTicketDetailById(id);
        log.info("FROM DBS {}, {}, {}", id, version, ticketDetail);

        // 5. check ticketitem
        if (ticketDetail != null) { // Nói sau khi code xong: Code nay co van de -> Gia su ticketItem lay ra tu dbs null thi sao, query mãi
            // 6. set cache
            redisInfrasService.setObject(genEventItemKey(id), ticketDetail);
        }
        return ticketDetail;
    }

    // CHƯA VIP LẮM - KHI HỌ REVIEW CODE - SẼ BẮT VIẾT LẠI
    public TicketDetail getTicketDefaultCacheVip(Long id, Long version) {
        log.info("Implement getTicketDefaultCacheVip->, {}, {} ", id, version);
//        TicketDetail ticketDetail = ticketDetailDomainService.getTicketDetailById(id);//redisInfrasService.getObject(genEventItemKey(id), TicketDetail.class);
        // 2. YES
        TicketDetail ticketDetail = redisInfrasService.getObject(genEventItemKey(id), TicketDetail.class);

        if (ticketDetail != null) {
//            log.info("FROM CACHE EXIST {}",ticketDetail);
            return ticketDetail;
        }
//        log.info("CACHE NO EXIST, START GET DB AND SET CACHE->, {}, {} ", id, version);
        // Tao lock process voi KEY
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock("PRO_LOCK_KEY_ITEM"+id);
        try {
            // 1 - Tao lock
            boolean isLock = locker.tryLock(1, 5, TimeUnit.SECONDS);
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            if (!isLock) {
//                log.info("LOCK WAIT ITEM PLEASE....{}", version);
                return ticketDetail;
            }
            // Get cache
            ticketDetail = redisInfrasService.getObject(genEventItemKey(id), TicketDetail.class);
            // 2. YES
            if (ticketDetail != null) {
//                log.info("FROM CACHE NGON A {}, {}, {}", id, version, ticketDetail);
                return ticketDetail;
            }
            // 3 -> van khong co thi truy van DB

            ticketDetail = ticketDetailDomainService.getTicketDetailById(id);
            log.info("FROM DBS ->>>> {}, {}", ticketDetail, version);
            if (ticketDetail == null) { // Neu trong dbs van khong co thi return ve not exists;
                log.info("TICKET NOT EXITS....{}", version);
                // set
                redisInfrasService.setObject(genEventItemKey(id), ticketDetail);
                return ticketDetail;
            }

            // neu co thi set redis
            redisInfrasService.setObject(genEventItemKey(id), ticketDetail); // TTL
            return ticketDetail;

            // OK XONG, chung ta review code nay ok ... ddau vaof DDD thoi nao
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            locker.unlock();
        }
    }

    private TicketDetailCache getTicketDetailLocalCache(Long id) {
        try {
            return TICKET_DETAIL_CACHE.getIfPresent(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public TicketDetailCache getTicketDetailDistributedCache(Long ticketId, Long version) {
        // 2 Get cache Redis
        TicketDetailCache ticketDetailCache = redisInfrasService.getObject(genEventItemKey(ticketId), TicketDetailCache.class);

        if (ticketDetailCache == null) {
            log.info("GET TICKET FROM DISTRIBUTED LOCK");
            ticketDetailCache = getTicketDetailDatabase(ticketId);
        }
        TICKET_DETAIL_CACHE.put(ticketId, ticketDetailCache);
        log.info("GET TICKET FROM DISTRIBUTED CACHE");
        return ticketDetailCache;
    }

    public TicketDetailCache getTicketDetailDatabase(Long ticketId) {
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock(genEventItemKey(ticketId));
        try {
            // 1 - Tao lock
            boolean isLock = locker.tryLock(1, 5, TimeUnit.SECONDS);

            if (!isLock) {
//                log.info("LOCK WAIT ITEM PLEASE....{}", version);
                return null;
            }
            // Get cache
            TicketDetailCache ticketDetailCache = redisInfrasService.getObject(genEventItemKey(ticketId), TicketDetailCache.class);
            // 2. YES
            if (ticketDetailCache != null) {
//                log.info("FROM CACHE NGON A {}, {}, {}", id, version, ticketDetail);
//                TICKET_DETAIL_CACHE.put(id, ticketDetail); // Item to local
                return ticketDetailCache;
            }
            // 3 -> van khong co thi truy van DB

            TicketDetail ticketDetail = ticketDetailDomainService.getTicketDetailById(ticketId);
            ticketDetailCache = new TicketDetailCache().withClone(ticketDetail).withVersion(System.currentTimeMillis());
//            log.info("FROM DBS ->>>> {}, {}", ticketDetail, version);
//            if (ticketDetail == null) { // Neu trong dbs van khong co thi return ve not exists;
////                log.info("TICKET NOT EXITS....{}", version);
//                // set
//                redisInfrasService.setObject(genEventItemKey(ticketId), ticketDetailCache);
////                TICKET_DETAIL_CACHE.put(id, null); // Item to local
////                return ticketDetail;
//            }

            // neu co thi set redis
            redisInfrasService.setObject(genEventItemKey(ticketId), ticketDetailCache);
            return ticketDetailCache;
            // OK XONG, chung ta review code nay ok ... ddau vaof DDD thoi nao
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {

            locker.unlock();
        }
    }

    //Cache local
    public TicketDetailCache getTicketDefaultCacheLocal(Long ticketId, Long version) {
        log.info("Implement getTicketDefaultCacheVip->, {}, {} ", ticketId, version);
        // 1 Get ticket Item
        TicketDetailCache ticketDetailCache = getTicketDetailLocalCache(ticketId);
        // Use:version, cache:version
        if (ticketDetailCache != null) {
            if (version == null) {
                log.info("01: GET TICKET FROM LOCAL CACHE: versionUser {}, versionLocal {}", version, ticketDetailCache.getVersion());
                return  ticketDetailCache;
            }

            if (version.equals(ticketDetailCache.getVersion())) {
                log.info("02: GET TICKET FROM LOCAL CACHE: versionUser {}, versionLocal {}", version, ticketDetailCache.getVersion());
                return  ticketDetailCache;
            }

            if (version < ticketDetailCache.getVersion()) {
                log.info("03: GET TICKET FROM LOCAL CACHE: versionUser {}, versionLocal {}", version, ticketDetailCache.getVersion());
                return  ticketDetailCache;
            }

            if (version > ticketDetailCache.getVersion()) {
                return  getTicketDetailDistributedCache(ticketId, version);
            }

            return ticketDetailCache;
        }

        return getTicketDetailDatabase(ticketId);
    }

    private String genEventItemKey(Long itemId) {
        return "PRO_TICKET:ITEM:" + itemId;
    }
}
