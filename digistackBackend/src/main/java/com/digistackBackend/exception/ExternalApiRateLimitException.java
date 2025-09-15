package com.digistackBackend.exception;

public class ExternalApiRateLimitException extends RuntimeException{
    public ExternalApiRateLimitException(String message){
        super(message);
    }
}
