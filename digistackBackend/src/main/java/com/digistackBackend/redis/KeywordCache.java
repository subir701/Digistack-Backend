package com.digistackBackend.redis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeywordCache implements Serializable {

    // identity
    private String keyword;              // normalized

//    @JsonProperty("location_code")
//    private Integer locationCode;        // e.g., 2840 (US)
//
//    @JsonProperty("language_code")
//    private String languageCode;         // e.g., "en"
//
//    @JsonProperty("search_partners")
//    private Boolean searchPartners;      // from response (or your request)
//
//    @JsonProperty("date_from")
//    private LocalDate dateFrom;          // optional: what you sent in request

    // metrics (from tasks[0].result[0])
    private String competition;          // "HIGH" | "MEDIUM" | "LOW"

    @JsonProperty("competition_index")
    private Integer competitionIndex;    // 0..100

    @JsonProperty("search_volume")
    private Integer searchVolume;        // aggregated sv

    @JsonProperty("low_top_of_page_bid")
    private BigDecimal lowTopOfPageBid;  // low_top_of_page_bid

    @JsonProperty("high_top_of_page_bid")
    private BigDecimal highTopOfPageBid; // high_top_of_page_bid
    private BigDecimal cpc;              // cpc

    // trend
    @JsonProperty("monthly_searches")
    private List<MonthlySearch> monthlySearches; // last 24–36 months typically

    // bookkeeping
//    private String provider;             // "dataforseo_google_ads"
//    private String taskId;               // tasks[0].id (for traceability)
//    private String providerVersion;      // response.version
//    private Instant cachedAt;            // when we cached it (UTC)


}
