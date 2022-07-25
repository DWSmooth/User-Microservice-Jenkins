package com.smoothstack.usermicroservice.controller;

import com.smoothstack.common.exceptions.*;
import com.smoothstack.common.models.User;
import com.smoothstack.usermicroservice.data.UserInformationBuild;
import com.smoothstack.usermicroservice.data.rest.ResetPasswordBody;
import com.smoothstack.usermicroservice.data.rest.SendConfirmEmailBody;
import com.smoothstack.usermicroservice.data.rest.SendResetPasswordBody;
import com.smoothstack.usermicroservice.service.ConfirmationService;

import com.smoothstack.usermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("user")
public class UserController {
    
    @Autowired
    ConfirmationService confirmationService;
    @Autowired
    UserService userService;

    /**
     * Gets a list of all users and user ids in the database
     */
    @GetMapping("all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Gets a list of all users + userInformation and user ids in the database
     */
    @GetMapping("allUserInformation")
    public List<UserInformationBuild> getAllUserInformation() {
        return userService.getAllUserInformation();
    }




    @GetMapping("username/{username}")
    public User getUserByUserName(@PathVariable(name = "username") String username) {
        try {
            User user = userService.getUserByUsername(username);
            return user;
        } catch(UserNotFoundException e ){
            System.out.println("username: " + username);
            return null;
        }
    }

    /**
     * The url used to get a usersInformation
     *
     * @param userId
     * @return 201 on success, 400 on invalid parameters, or 500 in case of database error
     */
    @GetMapping("userInformation/id/{userId}")
    public UserInformationBuild getUserInformationByUserId(@PathVariable(name="userId") Integer userId){
        try {
            UserInformationBuild userInformationBuild = userService.getUserInformationById(userId);
            return userInformationBuild;
        } catch(Exception e){
            System.out.print("userId: " + userId);
        }
        return null;
    }

    @GetMapping("id/{userId}")
    public User getUserByUserId(@PathVariable(name = "userId") Integer userId) {
        //TODO
        return null;
    }

    /**
     * The url used to create a user
     *
     * @param user
     * @return 201 on success, 400 on invalid parameters, or 500 in case of database error
     */
    @PostMapping(value = "create-user")
    public ResponseEntity createUser(@RequestBody User user) {
        try {
            Integer createdId = userService.createUser(user);
            return ResponseEntity.accepted().body("User created with id:" + createdId);
        } catch (InsufficientInformationException | UsernameTakenException | InsufficientPasswordException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * The url used to create a user + userInformation
     *
     * @param userInformationBuild
     * @return 201 on success, 400 on invalid parameters, or 500 in case of database error
     */
    @PostMapping(value = "create-user-information")
    public ResponseEntity createUserInformation(@RequestBody UserInformationBuild userInformationBuild) {
        try {
            Integer createdId = userService.createUserInformation(userInformationBuild);
            return ResponseEntity.accepted().body("User created with id:" + createdId);
        } catch (InsufficientInformationException | UsernameTakenException | InsufficientPasswordException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value="update-user")
    public ResponseEntity updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return ResponseEntity.accepted().body("Updated user");
        } catch (InsufficientInformationException | UsernameTakenException | InsufficientPasswordException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * The url used to update a user + userInformation
     *
     * @param userInformationBuild
     * @return 201 on success, 400 on invalid parameters, or 500 in case of database error
     */
    @PostMapping(value = "update-user-information")
    public ResponseEntity updateUserInformation(@RequestBody UserInformationBuild userInformationBuild) {
        try {
            userService.updateUserInformation(userInformationBuild);
            return ResponseEntity.accepted().body("Updated userInformation");
        } catch (InsufficientInformationException | UsernameTakenException | InsufficientPasswordException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value="delete-user")
    public ResponseEntity deleteUser(@RequestBody User user) {
        //TODO
        return null;
    }

    /**
     * The url used to update a user + userInformation
     *
     * @param userInformationBuild
     * @return 201 on success, 400 on invalid parameters, or 500 in case of database error
     */
    @PostMapping(value = "set-user-active-information")
    public ResponseEntity setUserActiveInformation(@RequestBody UserInformationBuild userInformationBuild) {
        try {
            userService.setUserActiveInformation(userInformationBuild);
            return ResponseEntity.accepted().body("User account status updated.");
        } catch (InsufficientInformationException | UsernameTakenException | InsufficientPasswordException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping(value = "confirmationMessage")
    public ResponseEntity<String> confirmationMessage(@RequestBody SendConfirmEmailBody body) {
        try {
            confirmationService.sendConfirmEmail(body);
            return ResponseEntity.status(HttpStatus.OK).body("Sent successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (SendMsgFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send");
        }
    }

    @PostMapping(value = "resetPasswordMessage")
    public ResponseEntity<String> resetPasswordMessage(@RequestBody SendResetPasswordBody body) {
        try {
            confirmationService.sendResetPassword(body);
            return ResponseEntity.status(HttpStatus.OK).body("Sent successfully");
        } catch (SendMsgFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send");
        }
    }

    @PutMapping(value = "confirmation")
    public ResponseEntity<String> confirmation(@RequestParam(name = "token") String token) {
        try {
            confirmationService.confirmEmail(token);
            return ResponseEntity.status(HttpStatus.OK).body("Email confirmed");
        } catch (TokenInvalidException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }
    }

    @PostMapping(value = "resetPassword")
    public ResponseEntity<String> resetPassword(
            @RequestParam(name = "token") String token,
            @RequestBody ResetPasswordBody body) {
        try {
            confirmationService.resetPassword(token, body);
            return ResponseEntity.status(HttpStatus.OK).body("Password set successfully");
        } catch (TokenInvalidException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        } catch (InsufficientPasswordException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password does not meet requirements");
        } catch (MsgInvalidException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalid or expired");
        }
    }
}
