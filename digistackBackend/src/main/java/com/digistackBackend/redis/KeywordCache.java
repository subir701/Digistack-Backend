package com.digistackBackend.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeywordCache {

    // identity
    private String keyword;              // normalized
    private Integer locationCode;        // e.g., 2840 (US)
    private String languageCode;         // e.g., "en"
    private Boolean searchPartners;      // from response (or your request)
    private LocalDate dateFrom;          // optional: what you sent in request

    // metrics (from tasks[0].result[0])
    private String competition;          // "HIGH" | "MEDIUM" | "LOW"
    private Integer competitionIndex;    // 0..100
    private Integer searchVolume;        // aggregated sv
    private BigDecimal lowTopOfPageBid;  // low_top_of_page_bid
    private BigDecimal highTopOfPageBid; // high_top_of_page_bid
    private BigDecimal cpc;              // cpc

    // trend
    private List<MonthlySearch> monthlySearches; // last 24–36 months typically

    // bookkeeping
    private String provider;             // "dataforseo_google_ads"
    private String taskId;               // tasks[0].id (for traceability)
    private String providerVersion;      // response.version
    private Instant cachedAt;            // when we cached it (UTC)
}
