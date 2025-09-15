package com.digistackBackend.controller;

import com.digistackBackend.exception.QuotaExceededException;
import com.digistackBackend.redis.UserQuota;
import com.digistackBackend.service.UserQuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/quota")
@RequiredArgsConstructor
public class UserQuotaController {

    private final UserQuotaService userQuotaService;

    @PostMapping("/{userId}/validate")
    public ResponseEntity<String> validateQuota(@PathVariable UUID userId) throws QuotaExceededException {
        userQuotaService.validateAndConsumeQuota(userId);
        return ResponseEntity.ok("Quota consumed successfully");
    }

    @PostMapping("/{userId}/revert")
    public ResponseEntity<String> revertQuota(@PathVariable UUID userId) {
        userQuotaService.revertQuota(userId);
        return ResponseEntity.ok("Quota reverted");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserQuota> getQuota(@PathVariable UUID userId) {
        return ResponseEntity.ok(userQuotaService.getQuota(userId));
    }
}
