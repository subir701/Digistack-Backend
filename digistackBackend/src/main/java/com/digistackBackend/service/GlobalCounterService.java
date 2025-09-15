package com.digistackBackend.service;

import com.digistackBackend.dto.MonthlyUsageResponseDTO;
import com.digistackBackend.exception.QuotaExceededException;
import com.digistackBackend.model.MonthlyUsage;

public interface GlobalCounterService {

    public MonthlyUsageResponseDTO incrementGlobalCounter(int amount)throws QuotaExceededException;

    public MonthlyUsageResponseDTO getCurrentMonthUsage();
}
