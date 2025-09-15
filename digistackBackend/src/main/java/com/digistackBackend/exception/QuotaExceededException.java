package com.digistackBackend.exception;

public class QuotaExceededException extends RuntimeException{
    public QuotaExceededException(String message){
        super(message);
    }
}
