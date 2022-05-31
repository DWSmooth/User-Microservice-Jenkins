package com.smoothstack.usermicroservice.exceptions;

public class InsufficientInformationException extends Exception{

    public InsufficientInformationException() {}

    public InsufficientInformationException(String message) {
        super(message);
    }
}
