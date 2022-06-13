package com.smoothstack.usermicroservice.service;

import com.smoothstack.common.models.User;
import com.smoothstack.common.repositories.UserRepository;
import com.smoothstack.common.services.CommonLibraryTestingService;
import com.smoothstack.usermicroservice.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

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
        assert(userService.usernameExists("testAdmin"));
        assert(userService.usernameExists("testDriver"));
        assert(!userService.usernameExists("non-existentUsername"));
        assert(!userService.usernameExists(null));
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
    void getUserByUsernameTest() {
        User test = null;

        try { test = userService.getUserByUsername("testAdmin"); }
        catch (UserNotFoundException e) {}
        finally { assert(test != null); }

        test = null;

        try { test = userService.getUserByUsername("non-existent"); }
        catch (UserNotFoundException e) {}
        finally { assert(test == null); }

        test = null;

        try { test = userService.getUserByUsername(null); }
        catch (UserNotFoundException e) {}
        finally { assert(test == null); }
    }

    @Test
    void getUserByIdTest() {
        User test = null;

        try { test = userService.getUserById(1); }
        catch (UserNotFoundException e) {}
        finally { assert(test != null); }

        test = null;

        try { test = userService.getUserById(0); }
        catch (UserNotFoundException e) {}
        finally { assert(test == null); }

        test = null;

        try { test = userService.getUserById(null); }
        catch (UserNotFoundException e) {}
        finally { assert(test == null); }
    }

    @Test
    @DirtiesContext
    void createUserTest() {
        User toAdd = new User();
        toAdd.setUserName("newTestUser");
        toAdd.setPassword("testPassword");
        Integer newUserId = null;

        try {
            newUserId = userService.createUser(toAdd);
        } catch (Exception e) {}

        assert(userService.usernameExists("newTestUser"));
        assert(userService.userIdExists(newUserId));
    }

    @Test
    @DirtiesContext
    void updateUserTest() {
        User user = null;

        try {
            user = userService.getUserByUsername("testAdmin");
        } catch (Exception e) {}

        assert(user != null);

        User updates = User.builder()
                .id(user.getId())
                .userName("differentUsername")
                .password("differentPassword")
                .build();

        try {
            userService.updateUser(updates);
        } catch (Exception e) {}

        assert(userService.usernameExists("differentUsername"));
    }
}
