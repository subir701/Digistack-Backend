package com.digistackBackend.service;

import com.digistackBackend.exception.QuotaExceededException;
import com.digistackBackend.redis.UserQuota;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserQuotaServiceImpl implements UserQuotaService{

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String USER_QUOTA_KEY = "user:%s:quota:%s";
    private static final String USER_MINUTE_BUCKET_KEY = "user:%s:bucket:%s"; // userId, minute
    private static final int MAX_DAILY_REQUESTS = 5;
    private static final int MAX_REQUESTS_PER_MINUTE = 12;

    // Lua script: atomically check daily & minute limits, and INCR both keys if allowed.
    // KEYS[1] = dayKey, KEYS[2] = bucketKey
    // ARGV[1] = maxDaily, ARGV[2] = maxMinute, ARGV[3] = dayTTLSeconds, ARGV[4] = bucketTTLSeconds
    private static final String QUOTA_LUA = """
    local day = tonumber(redis.call('GET', KEYS[1]) or '0')
    local bucket = tonumber(redis.call('GET', KEYS[2]) or '0')
    local maxDaily = tonumber(ARGV[1])
    local maxMinute = tonumber(ARGV[2])
    local dayTTL = tonumber(ARGV[3])
    local bucketTTL = tonumber(ARGV[4])
    if (day + 1) > maxDaily then
      return 'DAILY_LIMIT_EXCEEDED'
    end
    if (bucket + 1) > maxMinute then
      return 'MINUTE_LIMIT_EXCEEDED'
    end
    local dayVal = redis.call('INCR', KEYS[1])
    if dayVal == 1 then redis.call('EXPIRE', KEYS[1], dayTTL) end
    local bucketVal = redis.call('INCR', KEYS[2])
    if bucketVal == 1 then redis.call('EXPIRE', KEYS[2], bucketTTL) end
    return 'OK'
    """;

    private final DefaultRedisScript<String> quotaScript = new DefaultRedisScript<>(QUOTA_LUA, String.class);

    /**
     * Atomically validate and consume quota for the user.
     * Throws QuotaExceededException if daily or minute quota would be exceeded.
     */
    @Override
    public void validateAndConsumeQuota(UUID userId) throws QuotaExceededException {
        String today = LocalDate.now().toString();
        String dayKey = String.format(USER_QUOTA_KEY, userId, today);

        long currentMinute = System.currentTimeMillis() / 60000L;
        String bucketKey = String.format(USER_MINUTE_BUCKET_KEY, userId, currentMinute);

        // TTLs
        String dayTtl = String.valueOf(TimeUnit.DAYS.toSeconds(1));
        String bucketTtl = String.valueOf(60); // 60 seconds for minute bucket

        String res = redisTemplate.execute(quotaScript,
                Arrays.asList(dayKey, bucketKey),
                String.valueOf(MAX_DAILY_REQUESTS),
                String.valueOf(MAX_REQUESTS_PER_MINUTE),
                dayTtl,
                bucketTtl);

        if (!"OK".equals(res)) {
            if ("DAILY_LIMIT_EXCEEDED".equals(res)) {
                throw new QuotaExceededException("Daily quota exceeded (max " + MAX_DAILY_REQUESTS + ")");
            } else if ("MINUTE_LIMIT_EXCEEDED".equals(res)) {
                throw new QuotaExceededException("Rate limit exceeded (max " + MAX_REQUESTS_PER_MINUTE + " requests/minute)");
            } else {
                throw new QuotaExceededException("Quota check failed: " + res);
            }
        }
    }

    /**
     * Revert a previously-consumed quota (used when external API fails after we've consumed the quota)
     * This decrements both daily and minute counters by 1.
     */
    @Override
    public void revertQuota(UUID userId) {
        String today = LocalDate.now().toString();
        String dayKey = String.format("user:%s:quota:%s", userId, today);

        long currentMinute = System.currentTimeMillis() / 60000L;
        String bucketKey = String.format("user:%s:bucket:%s", userId, currentMinute);

        Long dayVal = redisTemplate.opsForValue().decrement(dayKey, 1);
        Long bucketVal = redisTemplate.opsForValue().decrement(bucketKey, 1);

        // Ensure counters don't become negative (fix if needed)
        if (dayVal != null && dayVal < 0) {
            redisTemplate.opsForValue().set(dayKey, 0);
        }
        if (bucketVal != null && bucketVal < 0) {
            redisTemplate.opsForValue().set(bucketKey, 0);
        }
    }

    @Override
    public UserQuota getQuota(UUID userId) {
        String today = LocalDate.now().toString();
        String key = String.format("user:%s:quota:%s", userId, today);
        Object v = redisTemplate.opsForValue().get(key);
        int count = 0;
        if (v instanceof Integer) count = (Integer) v;
        else if (v instanceof Long) count = ((Long) v).intValue();
        else if (v != null) count = Integer.parseInt(v.toString());

        return UserQuota.builder()
                .userId(userId)
                .date(today)
                .requestMade(count)
                .build();
    }
}
