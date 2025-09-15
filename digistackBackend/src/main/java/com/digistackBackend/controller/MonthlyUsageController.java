package com.digistackBackend.controller;

import com.digistackBackend.dto.MonthlyUsageRequestDTO;
import com.digistackBackend.dto.MonthlyUsageResponseDTO;
import com.digistackBackend.service.MonthlyUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/monthly-usage")
@RequiredArgsConstructor
public class MonthlyUsageController {

    private final MonthlyUsageService monthlyUsageService;

    @PostMapping
    public ResponseEntity<MonthlyUsageResponseDTO> addUsage(@RequestBody MonthlyUsageRequestDTO dto) {
        return ResponseEntity.ok(monthlyUsageService.addMonthlyUsage(dto));
    }

    @PutMapping("/{id}/increment")
    public ResponseEntity<MonthlyUsageResponseDTO> updateUsage(@PathVariable Long id, @RequestParam int credit) {
        return ResponseEntity.ok(monthlyUsageService.updateTotalKeywordUsed(id, credit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonthlyUsageResponseDTO> getUsage(@PathVariable Long id) {
        return ResponseEntity.ok(monthlyUsageService.getMonthlyUsage(id));
    }

    @GetMapping("/month/{month}")
    public ResponseEntity<MonthlyUsageResponseDTO> getUsageByMonth(@PathVariable String month) {
        return ResponseEntity.ok(monthlyUsageService.getMonthlyUsageByMonth(month));
    }
}