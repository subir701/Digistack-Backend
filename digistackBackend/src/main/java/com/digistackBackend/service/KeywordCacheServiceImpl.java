package com.digistackBackend.service;

import com.digistackBackend.dto.KeywordSearchRequestDTO;
import com.digistackBackend.dto.UserLocalCacheDTO;
import com.digistackBackend.exception.QuotaExceededException;
import com.digistackBackend.redis.KeywordCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordCacheServiceImpl implements KeywordCacheService{

    private final RedisTemplate<String, KeywordCache> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserQuotaService userQuotaService;
    private final GlobalCounterService globalCounterService;
    private final ExternalApiRateLimiterService externalApiRateLimiterService; // wrapper to call DataForSEO

    @Qualifier("asyncExecutor")
    private final Executor asyncExecutor;

    private static final String SERVER_CACHE_PREFIX = "keyword:%s";
    private static final Duration SERVER_TTL = Duration.ofHours(12);

    // If true, you charge quota even on cache hits (uncommon). Default false.
    private boolean chargeOnCacheHit = true;

    @Override
    public KeywordCache getKeywordData(KeywordSearchRequestDTO dto) {
        log.info("Entering getKeywordData for userId={} keyword={}", dto.getUserId(), dto.getKeywords());
        String keyword = normalize(dto.getKeywords());

        // 1️⃣ Check user local cache
        if (dto.getUserLocalCache() != null &&
                dto.getUserLocalCache().stream().anyMatch(c -> c.getKeyword().equalsIgnoreCase(keyword))) {
            log.info("User {} has local cache for keyword {}, skipping server fetch", dto.getUserId(), keyword);
            return null; // or special response
        }

        String serverKey = String.format(SERVER_CACHE_PREFIX, keyword);

        if (chargeOnCacheHit) userQuotaService.validateAndConsumeQuota(dto.getUserId());

        // 2️⃣ Check server Redis cache
        try {

            KeywordCache cached = redisTemplate.opsForValue().get(serverKey);
            if (cached != null) {
                log.info("Cache hit for keyword={}", keyword);

                return cached;
            }
        }catch(QuotaExceededException ex){
            throw new RuntimeException("Daily quota exceeded for user=" + dto.getUserId());
        }

        log.info("Cache miss for keyword={}, checking quota and external API", keyword);


        // 5️⃣ Prevent cache stampede
        String lockKey = "lock:" + keyword;
        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "locked", Duration.ofSeconds(5));
        if (Boolean.FALSE.equals(acquired)) {
            // Another thread/process fetching keyword, wait for cache
            int retries = 0;
            while (retries++ < 10) {
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                KeywordCache cached = redisTemplate.opsForValue().get(serverKey);
                if (cached != null) return cached;
            }
            throw new RuntimeException("Server busy, please retry");
        }

        try {
            // 6️⃣ Fetch from external API
            KeywordCache fresh = externalApiRateLimiterService.safeFetchKeyword(
                    keyword, dto.getLocation_code(), dto.getLanguage_code(), dto.getDate_from());
            globalCounterService.incrementGlobalCounter(1);

            log.info("Fetched fresh keyword from external API: {}", fresh);
            redisTemplate.opsForValue().set(serverKey, fresh, SERVER_TTL);
            return fresh;
        } finally {
            stringRedisTemplate.delete(lockKey);
        }
    }

//    Below is parllel processing code this this we are going to work in future
//    @Override
//    public CompletableFuture<KeywordCache> getKeywordData(KeywordSearchRequestDTO keywordSearchRequestDTO) {
//        return CompletableFuture.supplyAsync(() -> getKeywordDataInternal(keywordSearchRequestDTO), asyncExecutor);
//    }
//
//    private KeywordCache getKeywordDataInternal(KeywordSearchRequestDTO dto) {
//        log.info("Entering getKeywordData for userId={} keyword={}", dto.getUserId(), dto.getKeywords());
//        String keyword = normalize(dto.getKeywords());
//
//        // 1️⃣ Check user local cache
//        if (dto.getUserLocalCache() != null &&
//                dto.getUserLocalCache().stream().anyMatch(c -> c.getKeyword().equalsIgnoreCase(keyword))) {
//            log.info("User {} has local cache for keyword {}, skipping server fetch", dto.getUserId(), keyword);
//            return null; // or special response
//        }
//
//        String serverKey = String.format(SERVER_CACHE_PREFIX, keyword);
//
//        // 2️⃣ Check server Redis cache
//        try {
//
//            KeywordCache cached = redisTemplate.opsForValue().get(serverKey);
//            if (cached != null) {
//                log.info("Cache hit for keyword={}", keyword);
//                if (chargeOnCacheHit) userQuotaService.validateAndConsumeQuota(dto.getUserId());
//                return cached;
//            }
//        }catch(QuotaExceededException ex){
//            throw new RuntimeException("Daily quota exceeded for user=" + dto.getUserId());
//        }
//
//        log.info("Cache miss for keyword={}, checking quota and external API", keyword);
//
//
//        // 5️⃣ Prevent cache stampede
//        String lockKey = "lock:" + keyword;
//        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "locked", Duration.ofSeconds(5));
//        if (Boolean.FALSE.equals(acquired)) {
//            // Another thread/process fetching keyword, wait for cache
//            int retries = 0;
//            while (retries++ < 10) {
//                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
//                KeywordCache cached = redisTemplate.opsForValue().get(serverKey);
//                if (cached != null) return cached;
//            }
//            throw new RuntimeException("Server busy, please retry");
//        }
//
//        try {
//            // 6️⃣ Fetch from external API
//            KeywordCache fresh = externalApiRateLimiterService.safeFetchKeyword(
//                    keyword, dto.getLocation_code(), dto.getLanguage_code(), dto.getDate_from());
//            globalCounterService.incrementGlobalCounter(1);
//
//            log.info("Fetched fresh keyword from external API: {}", fresh);
//            redisTemplate.opsForValue().set(serverKey, fresh, SERVER_TTL);
//            return fresh;
//        } finally {
//            stringRedisTemplate.delete(lockKey);
//        }
//    }

    @Override
    public List<String> getCacheHistoryLast12Hours(List<UserLocalCacheDTO> userLocalCacheDTOList) {
        long cutoff = System.currentTimeMillis() - 12*60*60*1000L;
        return userLocalCacheDTOList.stream()
                .filter(entry -> entry.getCacheTimestamp() != 0L && entry.getCacheTimestamp() >= cutoff)
                .map(UserLocalCacheDTO::getKeyword)
                .collect(Collectors.toList());
    }

    private String normalize(String kw) {
        return kw == null ? "" : kw.trim().toLowerCase().replaceAll("\\s+", " ");
    }
}
