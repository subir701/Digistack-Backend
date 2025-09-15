package com.digistackBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyUsageResponseDTO {
    private Long id;
    private String month;
    private Integer totalKeywordUsed;
}
