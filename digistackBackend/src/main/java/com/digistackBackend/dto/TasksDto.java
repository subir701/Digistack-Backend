package com.digistackBackend.dto;

import com.digistackBackend.redis.KeywordCache;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TasksDto {

    private String id;

    @JsonProperty("status_code")
    private Integer statusCode;

    @JsonProperty("status_message")
    private String statusMessage;

    @JsonProperty("result_count")
    private Integer resultCount;

    private DataDto data;

    private List<KeywordCache> result;
}
