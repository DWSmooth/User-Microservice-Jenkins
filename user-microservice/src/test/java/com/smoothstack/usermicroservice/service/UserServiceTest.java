package com.smoothstack.usermicroservice.service;

import com.smoothstack.common.models.User;
import com.smoothstack.common.repositories.UserInformationRepository;
import com.smoothstack.common.repositories.UserRepository;
import com.smoothstack.common.repositories.UserRoleRepository;
import com.smoothstack.common.services.CommonLibraryTestingService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    CommonLibraryTestingService commonLibraryTestingService;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        commonLibraryTestingService.createTestData();
    }

    @Test
    void userNameExistsTest() {
        assert(userService.userNameExists("testAdmin"));
        assert(userService.userNameExists("testDriver"));
        assert(!userService.userNameExists("non-existentUsername"));
        assert(!userService.userNameExists(null));
    }

    @Test
    void userIdExistsTest() {
        Integer testId1 = userRepository.findTopByUserName("testAdmin").get().getId();
        Integer testId2 = userRepository.findTopByUserName("testDriver").get().getId();

        assert(userService.userIdExists(testId1));
        assert(userService.userIdExists(testId2));
        assert(!userService.userIdExists(0));
        assert(!userService.userIdExists(null));
    }

    @Test
    void createUserTest() {
        User toAdd = new User();
        toAdd.setUserName("newTestUser");
        toAdd.setPassword("testPassword");

        Integer newUserId = userService.createUser(toAdd);

        assert(userService.userNameExists("newTestUser"));
        assert(userService.userIdExists(newUserId));
    }
}
