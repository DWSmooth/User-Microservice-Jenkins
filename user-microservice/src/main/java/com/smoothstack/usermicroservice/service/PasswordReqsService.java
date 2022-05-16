package com.smoothstack.usermicroservice.service;

import org.springframework.stereotype.Service;

@Service
public class PasswordReqsService {
    private static Integer MIN_PASSWORD_LENGTH = 3;
    private static Integer MAX_PASSWORD_LENGTH = 256;

    public boolean verifyPassword(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH)
            return false;

        if (password.length() > MAX_PASSWORD_LENGTH)
            return false;

        return true;
    }
}
