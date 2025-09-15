package com.digistackBackend.service;

import com.digistackBackend.redis.KeywordCache;

import java.util.UUID;

public interface KeywordCacheService {
    public KeywordCache getKeywordData(UUID userId, String rawKeyword, int locationCode, String languageCode);
}
