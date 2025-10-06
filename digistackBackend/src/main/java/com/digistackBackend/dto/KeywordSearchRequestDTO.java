package com.digistackBackend.dto;

import com.digistackBackend.redis.KeywordCache;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeywordSearchRequestDTO {
    @NotNull(message = "UserId cannot be null")
    private UUID userId;
    @NotNull(message = "Location code cannot be null")
    private int location_code;
    @NotNull(message = "Language code cannot be null")
    private String language_code;
    @NotNull(message = "Keywords list cannot be null")
    private String keywords;
    @NotNull(message = "Date from cannot be null")
    private String date_from;
    @NotNull(message = "Search partners cannot be null")
    private boolean search_partners;
    // List of keywords client has cached locally, to avoid refetch
    private List<UserLocalCacheDTO> userLocalCache;
}
