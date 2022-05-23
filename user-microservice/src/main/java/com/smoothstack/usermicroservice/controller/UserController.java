package com.smoothstack.usermicroservice.controller;

import com.smoothstack.usermicroservice.data.rest.ResetPasswordBody;
import com.smoothstack.usermicroservice.data.rest.SendConfirmEmailBody;
import com.smoothstack.usermicroservice.data.rest.SendResetPasswordBody;
import com.smoothstack.usermicroservice.exceptions.*;
import com.smoothstack.usermicroservice.service.EmailConfirmationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    
    @Autowired
    EmailConfirmationService emailConfirmationService;

    @PostMapping(value = "ufd/user-service/sendConfirmEmail")
    public ResponseEntity<String> sendConfirmEmail(@RequestBody SendConfirmEmailBody body) {
        try {
            emailConfirmationService.sendConfirmEmail(body);
            return ResponseEntity.status(HttpStatus.OK).body("Sent successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (SendMsgFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send");
        }
    }

    @PostMapping(value = "ufd/user-service/sendResetPassword")
    public ResponseEntity<String> sendResetPassword(@RequestBody SendResetPasswordBody body) {
        try {
            emailConfirmationService.sendResetPassword(body);
            return ResponseEntity.status(HttpStatus.OK).body("Sent successfully");
        } catch (SendMsgFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send");
        }
    }

    @PutMapping(value = "ufd/user-service/confirmEmail")
    public ResponseEntity<String> confirmEmail(@RequestParam(name = "token") String token) {
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
