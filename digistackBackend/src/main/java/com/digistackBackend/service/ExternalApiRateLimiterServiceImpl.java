package com.digistackBackend.service;

import com.digistackBackend.redis.KeywordCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiRateLimiterServiceImpl implements ExternalApiRateLimiterService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ExternalKeywordApiClient externalApiClient;

    private static final int MAX_REQUESTS_PER_MINUTE = 12;
    private static final String TOKEN_KEY = "global:tokens:%s";
    private static final int RETRY_DELAY_MS = 3000;
    private static final int MAX_RETRIES = 3;

    @Override
    public KeywordCache safeFetchKeyword(String keyword, int locationCode, String languageCode, String dateFrom) {
        return safeFetchKeywordInternal(keyword, locationCode, languageCode, dateFrom, 0);
    }

    private KeywordCache safeFetchKeywordInternal(String keyword, int locationCode, String languageCode,
                                                  String dateFrom, int retryCount) {
        long currentMinute = System.currentTimeMillis() / 60000;
        String tokenKey = String.format(TOKEN_KEY, currentMinute);

        Long used = redisTemplate.opsForValue().increment(tokenKey, 1);
        if (used == 1) {
            redisTemplate.expire(tokenKey, 65, TimeUnit.SECONDS);
        }

        log.debug("Current token count for minute {}: {}", currentMinute, used);

        if (used != null && used > MAX_REQUESTS_PER_MINUTE) {
            if (retryCount >= MAX_RETRIES) {
                log.error("Max retry attempts reached for keyword {}; rejecting request", keyword);
                throw new RuntimeException("Too many requests, please try again later.");
            }
            log.warn("Request limit exceeded for keyword {}. Retry attempt {}", keyword, retryCount + 1);
            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Retry delay sleep interrupted", e);
            }
            return safeFetchKeywordInternal(keyword, locationCode, languageCode, dateFrom, retryCount + 1);
        }

        log.info("Calling external API for keyword {}", keyword);
        KeywordCache cache = externalApiClient.fetchKeywordMetrics(keyword, locationCode, languageCode, dateFrom);
        System.out.println("Printing data from external api client I at externalapiratelimiter "+cache.toString());
        return cache;
    }
}
