package com.smoothstack.usermicroservice.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PasswordReqsServiceTest {

    @Autowired
    PasswordReqsService pwService;

    @Test
    @Disabled
    void invalidLength() {
        String nullString = null;
        String emptyString = "";
        String tooFewChars = "ab";
        String tooManyChars = tooFewChars.repeat(1024);

        assert(!pwService.verifyPassword(nullString));
        assert(!pwService.verifyPassword(emptyString));
        assert(!pwService.verifyPassword(tooFewChars));
        assert(!pwService.verifyPassword(tooManyChars));
    }

    @Test
    @Disabled
    void validLength() {
        String a = "goodPassword123!";
        String b = "betterPasswordsAreAlwaysBetter";
        String c = "QDr67r3HqaKH^m$3$lR5lCmn0aqb6F01%nr1bGKCI@EKU#Zkvp4$WDw6@6cm";

        assert(pwService.verifyPassword(a));
        assert(pwService.verifyPassword(b));
        assert(pwService.verifyPassword(c));
    }
}
