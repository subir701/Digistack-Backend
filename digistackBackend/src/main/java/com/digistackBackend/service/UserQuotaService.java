package com.digistackBackend.service;

import com.digistackBackend.exception.QuotaExceededException;
import com.digistackBackend.redis.UserQuota;

import java.util.UUID;

public interface UserQuotaService {

    public void validateAndConsumeQuota(UUID userId)throws QuotaExceededException;

    public void revertQuota(UUID userId);

    public UserQuota getQuota(UUID userId);
}
