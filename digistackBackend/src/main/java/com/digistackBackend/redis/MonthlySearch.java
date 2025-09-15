package com.digistackBackend.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.AbstractList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySearch {
    private Integer year;
    private Integer month;
    private Integer searchVolume;
}
