package com.digistackBackend.controller;

import com.digistackBackend.dto.MonthlyUsageResponseDTO;
import com.digistackBackend.exception.QuotaExceededException;
import com.digistackBackend.service.GlobalCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/global")
@RequiredArgsConstructor
public class GlobalCounterController {

    private final GlobalCounterService globalCounterService;

    @PostMapping("/increment")
    public ResponseEntity<MonthlyUsageResponseDTO> incrementGlobal(@RequestParam(defaultValue = "1") int amount) throws QuotaExceededException {
        return ResponseEntity.ok(globalCounterService.incrementGlobalCounter(amount));
    }

    @GetMapping("/usage")
    public ResponseEntity<MonthlyUsageResponseDTO> getCurrentUsage() {
        return ResponseEntity.ok(globalCounterService.getCurrentMonthUsage());
    }
}