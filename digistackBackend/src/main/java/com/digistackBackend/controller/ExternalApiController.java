package com.digistackBackend.controller;

import com.digistackBackend.redis.KeywordCache;
import com.digistackBackend.service.ExternalApiRateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
public class ExternalApiController {

    private final ExternalApiRateLimiterService externalApiService;

    @GetMapping("/keyword")
    public ResponseEntity<KeywordCache> fetchKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1000") int locationCode,
            @RequestParam(defaultValue = "en") String languageCode,
            @RequestParam String dateFrom
    ) {
        KeywordCache cache = externalApiService.safeFetchKeyword(keyword, locationCode, languageCode,dateFrom);
        return ResponseEntity.ok(cache);
    }
}