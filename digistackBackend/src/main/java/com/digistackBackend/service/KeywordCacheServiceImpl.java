package com.digistackBackend.service;

import com.digistackBackend.redis.KeywordCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KeywordCacheServiceImpl implements KeywordCacheService{

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserQuotaService userQuotaService;
    private final GlobalCounterService globalCounterService;
    private final ExternalApiRateLimiterService externalApiRateLimiterService; // wrapper to call DataForSEO

    private static final String USER_CACHE_PREFIX = "user:%s:keyword:%s";
    private static final String SERVER_CACHE_PREFIX = "keyword:%s";
    private static final Duration SERVER_TTL = Duration.ofHours(12);
    private static final Duration USER_TTL = Duration.ofHours(24);

    // If true, you charge quota even on cache hits (uncommon). Default false.
    private boolean chargeOnCacheHit = false;

    public KeywordCache getKeywordData(UUID userId, String rawKeyword, int locationCode, String languageCode) {
        String keyword = normalize(rawKeyword);
        String userKey = String.format(USER_CACHE_PREFIX, userId, keyword);
        String serverKey = String.format(SERVER_CACHE_PREFIX, keyword);

        // 1) User cache
        KeywordCache userCache = (KeywordCache) redisTemplate.opsForValue().get(userKey);
        if (userCache != null) {
            if (chargeOnCacheHit) {
                // if you want to decrement quota even for cache hits:
                userQuotaService.validateAndConsumeQuota(userId); // will throw if exceeded
                // (we are not calling external API so no DB increment here)
            }
            return userCache;
        }

        // 2) Server cache
        KeywordCache serverCache = (KeywordCache) redisTemplate.opsForValue().get(serverKey);
        if (serverCache != null) {
            // Optionally charge quota on cache hit:
            if (chargeOnCacheHit) {
                userQuotaService.validateAndConsumeQuota(userId);
            }
            // populate user cache for faster next time (no DB changes)
            redisTemplate.opsForValue().set(userKey, serverCache, USER_TTL);
            return serverCache;
        }

        // 3) Cache miss → must consume user quota before hitting external API
        userQuotaService.validateAndConsumeQuota(userId);
        try {
            // Here you should use your token-bucket / queue manager to ensure 12 req/min
            // For clarity, I'll call the external API directly (but in production enqueue it).
            KeywordCache fresh = externalApiRateLimiterService.safeFetchKeyword(keyword, locationCode, languageCode);
            // Persist global usage (increments Redis and saves into monthly table atomically)
            globalCounterService.incrementGlobalCounter(1);

            // Save both caches
            redisTemplate.opsForValue().set(serverKey, fresh, SERVER_TTL);
            redisTemplate.opsForValue().set(userKey, fresh, USER_TTL);

            return fresh;
        } catch (Exception ex) {
            // External API failed → revert user quota consumption
            userQuotaService.revertQuota(userId);
            throw new RuntimeException("Failed to fetch keyword data", ex);
        }
    }

    private String normalize(String kw) {
        return kw == null ? "" : kw.trim().toLowerCase().replaceAll("\\s+", " ");
    }
}
