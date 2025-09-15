package com.digistackBackend.exception;

public class ExternalApiAuthException extends RuntimeException{
    public ExternalApiAuthException(String message){
        super(message);
    }
}
