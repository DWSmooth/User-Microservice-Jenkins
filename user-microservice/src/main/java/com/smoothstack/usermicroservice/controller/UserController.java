package com.smoothstack.usermicroservice.controller;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.smoothstack.common.models.User;
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

@RestController
public class UserController {
    
    @Autowired
    EmailConfirmationService emailConfirmationService;
    @Autowired
    UserService userService;

    /**
     * The url used to create a user
     *
     * @param user
     * @return 201 on success, 400 on invalid parameters, or 500 in case of database error
     */
    @PostMapping(value = "create-user")
    public ResponseEntity createUser(@RequestParam() User user) {
        if (user == null)
            return  ResponseEntity.badRequest().body("Could not resolve user");

        if (user.getUserName() == null)
            return ResponseEntity.badRequest().body("Username cannot be null");

        if (user.getPassword() == null)
            return ResponseEntity.badRequest().body("Password cannot be null");

        if (userService.userNameExists(user.getUserName()))
            return ResponseEntity.badRequest().body("Username is already in use");

        if (!userService.validPassword(user.getPassword()))
            return ResponseEntity.badRequest().body("Password does not fit criteria");

        Integer createdId = userService.createUser(user);

        if (createdId == null || createdId == 0)
            return ResponseEntity.internalServerError().body("Failed to confirm creation of user");

        return ResponseEntity.accepted().body(createdId);
    }

    @PostMapping(value="delete-user")
    public ResponseEntity deleteUser(@RequestBody User user) {
        if (user == null)
            return ResponseEntity.badRequest().body("Could not resolve user");

        if (user.getId() == null)
            return ResponseEntity.badRequest().body("User ID not provided");

        if (!userService.userIdExists(user.getId()))
            return ResponseEntity.badRequest().body("User not found under given ID");

        userService.deleteUser(user.getId());

        return ResponseEntity.accepted().body(user.getId());
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
