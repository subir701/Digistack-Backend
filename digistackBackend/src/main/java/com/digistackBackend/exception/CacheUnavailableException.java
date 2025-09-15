package com.digistackBackend.exception;

public class CacheUnavailableException extends RuntimeException{
    public CacheUnavailableException(String message){
        super(message);
    }
}
