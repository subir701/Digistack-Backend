package com.digistackBackend.service;

import com.digistackBackend.dto.MonthlyUsageRequestDTO;
import com.digistackBackend.dto.MonthlyUsageResponseDTO;
import com.digistackBackend.exception.ResourceNotFoundException;
import com.digistackBackend.mapper.MonthlyUsageMapper;
import com.digistackBackend.model.MonthlyUsage;
import com.digistackBackend.repository.MonthlyUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class MonthlyUsageServiceImpl implements MonthlyUsageService{

    private final MonthlyUsageRepository monthlyUsageRepository;

    @Override
    public MonthlyUsageResponseDTO addMonthlyUsage(MonthlyUsageRequestDTO requestDTO) {
        MonthlyUsage entity = MonthlyUsageMapper.toEnity(requestDTO);
        return MonthlyUsageMapper.toDto(monthlyUsageRepository.save(entity));
    }

    @Override
    public MonthlyUsageResponseDTO updateTotalKeywordUsed(Long id, Integer credit) {
        MonthlyUsage entity = monthlyUsageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MonthlyUsage not found with id " + id));

        entity.setTotalKeywordUsed(entity.getTotalKeywordUsed() + credit);
        return MonthlyUsageMapper.toDto(monthlyUsageRepository.save(entity));
    }

    @Override
    public MonthlyUsageResponseDTO getMonthlyUsage(Long id) {
        MonthlyUsage entity = monthlyUsageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MonthlyUsage not found with id " + id));

        return MonthlyUsageMapper.toDto(entity);
    }

    @Override
    public MonthlyUsageResponseDTO getMonthlyUsageByMonth(String month) {
        YearMonth ym = YearMonth.parse(month); // expects format "YYYY-MM"
        MonthlyUsage entity = monthlyUsageRepository.findByMonth(ym)
                .orElseThrow(() -> new ResourceNotFoundException("MonthlyUsage not found for month " + month));

        return MonthlyUsageMapper.toDto(entity);
    }
}
