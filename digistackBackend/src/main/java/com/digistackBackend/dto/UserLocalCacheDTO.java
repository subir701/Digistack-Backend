package com.digistackBackend.dto;

import lombok.Data;

@Data
public class UserLocalCacheDTO {

    private String keyword;
    private long cacheTimestamp;
}
