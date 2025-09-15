package com.digistackBackend.exception;

public class ExternalApiResponseException extends RuntimeException{
    public ExternalApiResponseException(String message){
        super(message);
    }
}
