package com.digistackBackend.exception;

public class MonthlyLimitExceededException extends RuntimeException{
    public MonthlyLimitExceededException(String message){
        super(message);
    }
}
