package com.digistackBackend.service;

import com.digistackBackend.redis.KeywordCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ExternalApiRateLimiterServiceImpl implements ExternalApiRateLimiterService{

    private final RedisTemplate<String, Object> redisTemplate;
    private final ExternalKeywordApiClient externalApiClient;

    private static final int MAX_REQUESTS_PER_MINUTE = 12;
    private static final String TOKEN_KEY = "global:tokens:%s";
    @Override
    public KeywordCache safeFetchKeyword(String keyword, int locationCode, String languageCode) {
        long currentMinute = System.currentTimeMillis()/ 60000;
        String tokenKey = String.format(TOKEN_KEY, currentMinute);

        Long used = redisTemplate.opsForValue().increment(tokenKey,1);
        if(used == 1){
            redisTemplate.expire(tokenKey, 65, TimeUnit.SECONDS);
        }

        if(used != null && used > MAX_REQUESTS_PER_MINUTE){
            try{
                Thread.sleep(3000);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
            return safeFetchKeyword(keyword,locationCode,languageCode);
        }

        return externalApiClient.fetchKeywordMetrics(keyword, locationCode, languageCode);
    }
}
