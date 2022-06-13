package com.smoothstack.usermicroservice.controller;

import com.smoothstack.common.models.User;
import com.smoothstack.common.services.CommonLibraryTestingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class UserControllerTest {
    @Autowired
    UserController userController;

    @Autowired
    CommonLibraryTestingService commonLibraryTestingService;

    @BeforeEach()
    void setUp() {
        commonLibraryTestingService.createTestData();
    }

    @Test
    void createUserTest() {
        // Tests Accepted
        User testUser1 = new User();
        testUser1.setUserName("newUser");
        testUser1.setPassword("testPassword");
        ResponseEntity response = userController.createUser(testUser1);
        assert(response.getStatusCode().equals(HttpStatus.ACCEPTED));

        // Tests null user
        response = userController.createUser(null);
        assert(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assert(response.getBody().equals("Could not resolve user"));

        // Tests null username
        User testUser2 = new User();
        testUser2.setPassword("somePassword");
        response = userController.createUser(testUser2);
        assert(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assert(response.getBody().equals("Username cannot be null"));

        // Tests null password
        User testUser3 = new User();
        testUser3.setUserName("testMissingPassword");
        response = userController.createUser(testUser3);
        assert(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assert(response.getBody().equals("Password cannot be null"));

        // Tests username exists
        User testUser4 = new User();
        testUser4.setUserName("newUser");
        testUser4.setPassword("somePassword");
        response = userController.createUser(testUser4);
        assert(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assert(response.getBody().equals("Username is already in use"));
    }

    @Test
    void deleteUserTest() {
        // Test Accepted
        User testUser = new User();
        testUser.setId(4);
        ResponseEntity response = userController.deleteUser(testUser);
        assert(response.getStatusCode().equals(HttpStatus.ACCEPTED));

        // Test Null User
        response = userController.deleteUser(null);
        assert(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assert(response.getBody().equals("Could not resolve user"));

        // Test no user id
        testUser = new User();
        response = userController.deleteUser(testUser);
        assert(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assert(response.getBody().equals("User ID not provided"));

        // Test user not found with id
        testUser = new User();
        testUser.setId(0);
        response = userController.deleteUser(testUser);
        assert(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assert(response.getBody().equals("User not found under given ID"));

    }
}
