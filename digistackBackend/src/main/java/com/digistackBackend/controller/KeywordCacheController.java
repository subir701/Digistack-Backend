package com.digistackBackend.controller;

import com.digistackBackend.dto.KeywordSearchRequestDTO;
import com.digistackBackend.dto.UserLocalCacheDTO;
import com.digistackBackend.redis.KeywordCache;
import com.digistackBackend.service.KeywordCacheService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("/api/keywords")
@RequiredArgsConstructor
public class KeywordCacheController {

    private final KeywordCacheService keywordCacheService;
    @Qualifier("asyncExecutor")
    private final Executor asyncExecutor;

    @PostMapping("/fetch")
    public ResponseEntity<KeywordCache> getKeywordData(@Valid @RequestBody KeywordSearchRequestDTO keywordSearchRequestDTO) {
        return ResponseEntity.ok(keywordCacheService.getKeywordData(keywordSearchRequestDTO));
    }

    @PostMapping("/history")
    public ResponseEntity<List<String>> getCacheHistory(@RequestBody List<UserLocalCacheDTO> userLocalCacheDTOList) {
        List<String> recentCacheKeywords = keywordCacheService.getCacheHistoryLast12Hours(userLocalCacheDTOList);
        return ResponseEntity.ok(recentCacheKeywords);
    }
}
