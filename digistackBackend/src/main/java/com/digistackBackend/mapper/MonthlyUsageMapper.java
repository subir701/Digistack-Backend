package com.digistackBackend.mapper;

import com.digistackBackend.dto.MonthlyUsageRequestDTO;
import com.digistackBackend.dto.MonthlyUsageResponseDTO;
import com.digistackBackend.model.MonthlyUsage;

import java.time.YearMonth;

public final class MonthlyUsageMapper {
    private MonthlyUsageMapper(){}

    public static MonthlyUsage toEnity(MonthlyUsageRequestDTO request){
        if(request == null)return null;

        YearMonth ym = parseYearMonth(request.getMonth());
        MonthlyUsage monthlyUsage = new MonthlyUsage();
        //id is set by JPA; not available in request DTO

        monthlyUsage.setMonth(ym);
        monthlyUsage.setTotalKeywordUsed(request.getTotalKeywordUsed());
        return monthlyUsage;
    }

    public static MonthlyUsageResponseDTO toDto(MonthlyUsage monthlyUsage){
        if(monthlyUsage == null)return null;

        return MonthlyUsageResponseDTO.builder()
                .id(monthlyUsage.getId())
                .month(monthlyUsage.getMonth() != null ? monthlyUsage.getMonth().toString(): null)
                .totalKeywordUsed(monthlyUsage.getTotalKeywordUsed())
                .build();
    }

    public static void updateEntityFromDto(MonthlyUsageRequestDTO dto, MonthlyUsage entity) {
        if (dto == null || entity == null) return;
        YearMonth ym = parseYearMonth(dto.getMonth());
        if (ym != null) entity.setMonth(ym);
        if (dto.getTotalKeywordUsed() != null) entity.setTotalKeywordUsed(dto.getTotalKeywordUsed());
    }

    private static YearMonth parseYearMonth(String monthStr) {
        if (monthStr == null || monthStr.isBlank()) return null;
        // Expect "YYYY-MM"
        try {
            return YearMonth.parse(monthStr);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid month format, expected YYYY-MM, got: " + monthStr, ex);
        }
    }
}
