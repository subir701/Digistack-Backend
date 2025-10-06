package com.digistackBackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataForSeoResponeDTO {

    private String version;
//    private HttpStatusCode statusCode;

    @JsonProperty("status_code")
    private Integer statusCode;

    @JsonProperty("status_message")
    private String statusMessage;

    private List<TasksDto> tasks;
}
