package com.digistackBackend.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GlobalCounter {

    private String month;
    private Integer totalKeywordsUsed;
}
