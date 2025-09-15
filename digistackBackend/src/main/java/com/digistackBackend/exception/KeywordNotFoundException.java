package com.digistackBackend.exception;

public class KeywordNotFoundException extends RuntimeException{
    public KeywordNotFoundException(String message){
        super(message);
    }
}
