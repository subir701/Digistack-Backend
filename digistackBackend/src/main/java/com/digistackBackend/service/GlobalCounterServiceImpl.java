package com.digistackBackend.service;

import com.digistackBackend.dto.MonthlyUsageResponseDTO;
import com.digistackBackend.exception.QuotaExceededException;
import com.digistackBackend.mapper.MonthlyUsageMapper;
import com.digistackBackend.model.MonthlyUsage;
import com.digistackBackend.repository.MonthlyUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GlobalCounterServiceImpl implements GlobalCounterService{

    private final RedisTemplate<String, Object> redisTemplate;
    private final MonthlyUsageRepository monthlyUsageRepository;

    private static final String GLOBAL_COUNTER_KEY = "global:month_counter:%s";

    @Value("${monthly.total.credit}")
    private static int MAX_MONTHLY_CREDITS;

    // Lua: check (curr + amount) <= max then INCRBY, else return error string
    // KEYS[1] = monthKey
    // ARGV[1] = amount, ARGV[2] = maxMonthly, ARGV[3] = ttlSeconds
    private static final String GLOBAL_LUA = """
    local curr = tonumber(redis.call('GET', KEYS[1]) or '0')
    local amount = tonumber(ARGV[1])
    local max = tonumber(ARGV[2])
    local ttl = tonumber(ARGV[3])
    if (curr + amount) > max then
      return 'MONTHLY_LIMIT_EXCEEDED'
    end
    local res = redis.call('INCRBY', KEYS[1], amount)
    if res == amount then redis.call('EXPIRE', KEYS[1], ttl) end
    return tostring(res)
    """;

    private final DefaultRedisScript<String> globalScript = new DefaultRedisScript<>(GLOBAL_LUA, String.class);

    @Override
    public MonthlyUsageResponseDTO incrementGlobalCounter(int amount) throws QuotaExceededException {
        YearMonth currentMonth = YearMonth.now();
        String key = String.format(GLOBAL_COUNTER_KEY, currentMonth);

        int ttlDays = 40; // safe buffer
        String res = redisTemplate.execute(globalScript,
                Arrays.asList(key),
                String.valueOf(amount),
                String.valueOf(MAX_MONTHLY_CREDITS),
                String.valueOf(TimeUnit.DAYS.toSeconds(ttlDays)));

        if (res == null) {
            throw new QuotaExceededException("Global counter update failed (null response)");
        }
        if ("MONTHLY_LIMIT_EXCEEDED".equals(res)) {
            throw new QuotaExceededException("Global monthly quota exceeded (max " + MAX_MONTHLY_CREDITS + ")");
        }

        long newCount = Long.parseLong(res);

        // Now persist to DB. If DB save fails, revert Redis increment.
        try {
            Optional<MonthlyUsage> usageOpt = monthlyUsageRepository.findByMonth(currentMonth);
            MonthlyUsage usage = usageOpt.orElseGet(() -> new MonthlyUsage(0L, currentMonth, 0));
            usage.setTotalKeywordUsed((usage.getTotalKeywordUsed() == null ? 0 : usage.getTotalKeywordUsed()) + amount);
            MonthlyUsage saved = monthlyUsageRepository.save(usage);
            return MonthlyUsageMapper.toDto(saved);
        } catch (Exception dbEx) {
            // rollback Redis increment
            redisTemplate.opsForValue().increment(key, -amount);
            throw new RuntimeException("Failed to persist monthly usage, rolled back global counter", dbEx);
        }
    }

    @Override
    public MonthlyUsageResponseDTO getCurrentMonthUsage() {
        YearMonth currentMonth = YearMonth.now();
        return monthlyUsageRepository.findByMonth(currentMonth)
                .map(MonthlyUsageMapper::toDto)
                .orElseGet(() -> new MonthlyUsageResponseDTO(0L, currentMonth.toString(), 0));
    }

}
