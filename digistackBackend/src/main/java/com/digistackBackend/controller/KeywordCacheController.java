package com.digistackBackend.controller;

import com.digistackBackend.redis.KeywordCache;
import com.digistackBackend.service.KeywordCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/keywords")
@RequiredArgsConstructor
public class KeywordCacheController {

    private final KeywordCacheService keywordCacheService;

    @GetMapping("/fetch")
    public ResponseEntity<KeywordCache> getKeywordData(
            @RequestParam UUID userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1000") int locationCode,
            @RequestParam(defaultValue = "en") String languageCode
    ) {
        KeywordCache cache = keywordCacheService.getKeywordData(userId, keyword, locationCode, languageCode);
        return ResponseEntity.ok(cache);
    }
}
