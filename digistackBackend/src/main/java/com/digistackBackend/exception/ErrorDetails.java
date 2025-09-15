package com.digistackBackend.exception;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {

    private String message;
    private String description;
    private LocalDateTime timestamp;

    public ErrorDetails(String message, String description){
        this.message=message;
        this.description=description;
    }

    @PostConstruct
    public void onCreate(){
        this.timestamp = LocalDateTime.now();
    }
}
