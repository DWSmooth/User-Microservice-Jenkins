package com.smoothstack.usermicroservice.controller;

import com.smoothstack.common.models.User;
import com.smoothstack.common.models.UserInformation;
import com.smoothstack.usermicroservice.data.UserInformationBuild;
import com.smoothstack.usermicroservice.data.rest.ResetPasswordBody;
import com.smoothstack.usermicroservice.data.rest.SendConfirmEmailBody;
import com.smoothstack.usermicroservice.data.rest.SendResetPasswordBody;
import com.smoothstack.usermicroservice.exceptions.*;
import com.smoothstack.usermicroservice.service.EmailConfirmationService;

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
    EmailConfirmationService emailConfirmationService;
    @Autowired
    UserService userService;

    /**
     * Gets a list of all users and user ids in the database
     */
    @GetMapping("all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("allUserInformation")
    public List<UserInformationBuild> getAllUserInformation() {
        return userService.getAllUserInformation();
    }

//    try {
//        userService.updateUser(user);
//        return ResponseEntity.accepted().body("Updated user");
//    } catch (InsufficientInformationException | UsernameTakenException | InsufficientPasswordException | UserNotFoundException e) {
//        return ResponseEntity.badRequest().body(e.getMessage());
//    }

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

    @GetMapping("userInformation/id/{userId}")
//    @GetMapping("userInformation/email/{userEmail}")
    public UserInformation getUserInformationByUserId(@PathVariable(name="userId") Integer userId){
//    public UserInformation getUserInformationByUserId(@PathVariable(name="userEmail") String userEmail){
        try {
            UserInformation userInformation = userService.getUserInformationById(userId);
            return userInformation;
        } catch(Exception e){
            System.out.print("userId: " + userId);
        }
        return null;
    }

    @GetMapping("id/{userId}")
    public User getUserByUserId(@PathVariable(name = "userId") Integer userId) {
        try {
            User user = userService.getUserById(userId);
            return user;
        } catch(UserNotFoundException e ){
            System.out.println("userId: " + userId);
            return null;
        }
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

    @PostMapping(value = "update-user-information")
    public ResponseEntity updateUserInformation(@RequestBody UserInformationBuild userInformationBuild) {
        try {
            userService.updateUserInformation(userInformationBuild);
            return ResponseEntity.accepted().body("Updated user");
        } catch (InsufficientInformationException | UsernameTakenException | InsufficientPasswordException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value="delete-user")
    public ResponseEntity deleteUser(@RequestBody User user) {
        //TODO
        return null;
    }

    @PostMapping(value = "ufd/user-service/confirmationMessage")
    public ResponseEntity<String> confirmationMessage(@RequestBody SendConfirmEmailBody body) {
        try {
            emailConfirmationService.sendConfirmEmail(body);
            return ResponseEntity.status(HttpStatus.OK).body("Sent successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (SendMsgFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send");
        }
    }

    @PostMapping(value = "ufd/user-service/resetPasswordMessage")
    public ResponseEntity<String> resetPasswordMessage(@RequestBody SendResetPasswordBody body) {
        try {
            emailConfirmationService.sendResetPassword(body);
            return ResponseEntity.status(HttpStatus.OK).body("Sent successfully");
        } catch (SendMsgFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send");
        }
    }

    @PutMapping(value = "ufd/user-service/confirmation")
    public ResponseEntity<String> confirmation(@RequestParam(name = "token") String token) {
        try {
            emailConfirmationService.confirmEmail(token);
            return ResponseEntity.status(HttpStatus.OK).body("Email confirmed");
        } catch (TokenInvalidException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }
    }

    @PostMapping(value = "ufd/user-service/resetPassword")
    public ResponseEntity<String> resetPassword(
            @RequestParam(name = "token") String token,
            @RequestBody ResetPasswordBody body) {
        try {
            emailConfirmationService.resetPassword(token, body);
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
