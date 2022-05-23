package com.smoothstack.usermicroservice.exceptions;

public class TokenInvalidException extends Exception {

    public TokenInvalidException() {}

    public TokenInvalidException(String message) {
        super(message);
    }

}
