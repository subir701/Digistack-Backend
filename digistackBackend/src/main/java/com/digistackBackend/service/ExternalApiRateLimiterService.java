package com.digistackBackend.service;

import com.digistackBackend.redis.KeywordCache;

public interface ExternalApiRateLimiterService {
    public KeywordCache safeFetchKeyword(String keyword, int locationCode, String languageCode, String dateFrom);
}
