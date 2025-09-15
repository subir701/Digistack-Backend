package com.digistackBackend.service;

import com.digistackBackend.redis.KeywordCache;

public interface ExternalKeywordApiClient {

    public KeywordCache fetchKeywordMetrics(String keyword, int locationCode, String languageCode);
}
