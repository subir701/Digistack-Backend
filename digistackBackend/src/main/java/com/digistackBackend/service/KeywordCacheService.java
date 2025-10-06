package com.digistackBackend.service;

import com.digistackBackend.dto.KeywordSearchRequestDTO;
import com.digistackBackend.dto.UserLocalCacheDTO;
import com.digistackBackend.redis.KeywordCache;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface KeywordCacheService {
//    public CompletableFuture<KeywordCache> getKeywordData(KeywordSearchRequestDTO keywordSearchRequestDTO);
    public KeywordCache getKeywordData(KeywordSearchRequestDTO keywordSearchRequestDTO);
    public List<String> getCacheHistoryLast12Hours(List<UserLocalCacheDTO> userLocalCacheDTOList);
}
