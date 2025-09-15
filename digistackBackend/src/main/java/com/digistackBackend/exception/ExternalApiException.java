package com.digistackBackend.exception;

public class ExternalApiException extends RuntimeException{
    public ExternalApiException(String message){
        super(message);
    }
}
