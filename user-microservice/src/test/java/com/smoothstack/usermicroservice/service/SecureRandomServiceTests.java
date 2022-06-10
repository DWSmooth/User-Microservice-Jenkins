package com.smoothstack.usermicroservice.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SecureRandomServiceTests {

    @Autowired
    SecureRandomService randomService;

    @Test
    @Disabled
    void generate() {
        for (int i = 1; i <= 256; i++) {
            String output = randomService.generateAlphanumericString(i);

            assert(output != null);
            assert(output.length() == i);
        }
    }
}
