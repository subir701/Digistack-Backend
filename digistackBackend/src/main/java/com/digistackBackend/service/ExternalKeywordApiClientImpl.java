package com.digistackBackend.service;

import com.digistackBackend.redis.KeywordCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@Service
public class ExternalKeywordApiClientImpl implements ExternalKeywordApiClient{

    private final Random random = new Random();

    @Override
    public KeywordCache fetchKeywordMetrics(String keyword, int locationCode, String languageCode) {
        try{
            Thread.sleep(2000 + random.nextInt(1000));
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }

        // Return dummy metrics
        return KeywordCache.builder()
                .keyword(keyword)
                .locationCode(locationCode)
                .languageCode(languageCode)
                .searchVolume(random.nextInt(10000))
                .build();
    }
}
