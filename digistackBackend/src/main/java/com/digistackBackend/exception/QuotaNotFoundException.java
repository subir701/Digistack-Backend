package com.digistackBackend.exception;

public class QuotaNotFoundException extends RuntimeException{
    public QuotaNotFoundException(String message){
        super(message);
    }
}
