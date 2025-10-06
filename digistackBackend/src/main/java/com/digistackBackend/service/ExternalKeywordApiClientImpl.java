package com.digistackBackend.service;

import com.digistackBackend.dto.DataForSeoResponeDTO;
import com.digistackBackend.redis.KeywordCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class ExternalKeywordApiClientImpl implements ExternalKeywordApiClient{

    private final Random random = new Random();

    @Value("${externl.api.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public KeywordCache fetchKeywordMetrics(String keyword, int locationCode, String languageCode, String dateFrom) {

        System.out.println("We are inside externalkeywordapiclientservice");
        log.info("fetchKeywordMetrics called for keyword: {}", keyword);

        try{
            Thread.sleep(2000 + random.nextInt(1000));
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            log.error("Thread interrupted during sleep", e);
        }


        Map<String, Object> requestBody = Map.of(
                "keywords", List.of(keyword),
                "location_code", locationCode,
                "language_code", languageCode,
                "date_from", dateFrom
        );

        log.info("Calling external API with requestBody: {}", requestBody);

        try {
            DataForSeoResponeDTO dto = restTemplate.postForObject(baseUrl, requestBody, DataForSeoResponeDTO.class);
            if (dto == null || dto.getTasks() == null || dto.getTasks().isEmpty()) {
                log.error("Empty or invalid response from external API");
                throw new RuntimeException("Invalid response from external API");
            }
            KeywordCache cache = dto.getTasks().get(0).getResult().get(0);
            log.info("Received keyword metrics for keyword: {}", keyword);
            System.out.println("print the the object response from external api "+cache.toString());
            return cache;
        } catch (Exception e) {
            log.error("Failed to fetch keyword metrics for keyword " + keyword, e);
            throw new RuntimeException("Failed to fetch keyword data", e);
        }
    }
}
