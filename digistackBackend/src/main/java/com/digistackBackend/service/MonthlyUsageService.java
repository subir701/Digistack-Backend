package com.digistackBackend.service;

import com.digistackBackend.dto.MonthlyUsageRequestDTO;
import com.digistackBackend.dto.MonthlyUsageResponseDTO;

public interface MonthlyUsageService {
    MonthlyUsageResponseDTO addMonthlyUsage(MonthlyUsageRequestDTO requestDTO);

    MonthlyUsageResponseDTO updateTotalKeywordUsed(Long id, Integer credit);

    MonthlyUsageResponseDTO getMonthlyUsage(Long id);

    MonthlyUsageResponseDTO getMonthlyUsageByMonth(String month);
}
