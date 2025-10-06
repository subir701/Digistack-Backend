package com.digistackBackend.service;

import com.digistackBackend.dto.MonthlyUsageRequestDTO;
import com.digistackBackend.dto.MonthlyUsageResponseDTO;
import com.digistackBackend.exception.ResourceNotFoundException;
import com.digistackBackend.mapper.MonthlyUsageMapper;
import com.digistackBackend.model.MonthlyUsage;
import com.digistackBackend.repository.MonthlyUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonthlyUsageServiceImpl implements MonthlyUsageService{

    private final MonthlyUsageRepository monthlyUsageRepository;

    @Override
    public MonthlyUsageResponseDTO addMonthlyUsage(MonthlyUsageRequestDTO requestDTO) {
        log.info("Adding new monthly usage for month: {}", requestDTO.getMonth());
        MonthlyUsage entity = MonthlyUsageMapper.toEnity(requestDTO);
        MonthlyUsage saved = monthlyUsageRepository.save(entity);
        log.info("Monthly usage added successfully: id={}, month={}", saved.getId(), saved.getMonth());
        return MonthlyUsageMapper.toDto(saved);
    }

    @Override
    public MonthlyUsageResponseDTO updateTotalKeywordUsed(Long id, Integer credit) {
        log.info("Updating totalKeywordUsed for id={} by {}", id, credit);
        MonthlyUsage entity = monthlyUsageRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed: MonthlyUsage not found for id={}", id);
                    return new ResourceNotFoundException("MonthlyUsage not found with id " + id);
                });

        entity.setTotalKeywordUsed(entity.getTotalKeywordUsed() + credit);
        MonthlyUsage updated = monthlyUsageRepository.save(entity);
        log.info("Updated monthly usage: id={}, new totalKeywordUsed={}", id, updated.getTotalKeywordUsed());
        return MonthlyUsageMapper.toDto(updated);
    }

    @Override
    public MonthlyUsageResponseDTO getMonthlyUsage(Long id) {
        log.info("Fetching monthly usage for id={}", id);
        MonthlyUsage entity = monthlyUsageRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Fetch failed: MonthlyUsage not found for id={}", id);
                    return new ResourceNotFoundException("MonthlyUsage not found with id " + id);
                });

        log.info("Fetched monthly usage: id={}, month={}", id, entity.getMonth());
        return MonthlyUsageMapper.toDto(entity);
    }

    @Override
    public MonthlyUsageResponseDTO getMonthlyUsageByMonth(String month) {
        log.info("Fetching monthly usage for month={}", month);
        YearMonth ym = YearMonth.parse(month); // expects format "YYYY-MM"
        MonthlyUsage entity = monthlyUsageRepository.findByMonth(ym)
                .orElseThrow(() -> {
                    log.warn("Fetch failed: MonthlyUsage not found for month={}", month);
                    return new ResourceNotFoundException("MonthlyUsage not found for month " + month);
                });

        log.info("Fetched monthly usage for month={}: id={}", month, entity.getId());
        return MonthlyUsageMapper.toDto(entity);
    }
}
