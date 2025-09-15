package com.digistackBackend.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("userQuota")
public class UserQuota {
    @Id
    private String id;
    private UUID userId;
    private String date;
    private Integer requestMade;
}
