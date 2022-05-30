package com.smoothstack.usermicroservice.exceptions;

public class UsernameTakenException extends Exception{
    public UsernameTakenException() {}

    public UsernameTakenException(String message) {
        super(message);
    }
}
