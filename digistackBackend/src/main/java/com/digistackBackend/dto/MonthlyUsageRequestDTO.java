package com.digistackBackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyUsageRequestDTO {
    @NotNull(message = "Month cannot be null")
    private String month;
    @NotNull(message = "Total Keyword used can be null")
    private Integer totalKeywordUsed;
}
