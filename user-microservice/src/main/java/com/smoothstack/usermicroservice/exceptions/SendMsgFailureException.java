package com.smoothstack.usermicroservice.exceptions;

public class SendMsgFailureException extends Exception {

    public SendMsgFailureException() {}

    public SendMsgFailureException(String message) {
        super(message);
    }

}