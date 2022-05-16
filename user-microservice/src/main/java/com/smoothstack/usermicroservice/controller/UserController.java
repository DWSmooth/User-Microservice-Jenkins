package com.smoothstack.usermicroservice.controller;

import com.smoothstack.usermicroservice.data.rest.ResetPasswordBody;
import com.smoothstack.usermicroservice.data.rest.SendConfirmEmailBody;
import com.smoothstack.usermicroservice.data.rest.SendResetPasswordBody;
import com.smoothstack.usermicroservice.service.EmailConfirmationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    
    @Autowired
    EmailConfirmationService emailConfirmationService;

    @PostMapping(value = "ufd/user-service/sendConfirmEmail")
    public String sendConfirmEmail(@RequestBody SendConfirmEmailBody body) {
        emailConfirmationService.sendConfirmEmail(body);
        return "Sent";
    }

    @PostMapping(value = "ufd/user-service/sendResetPassword")
    public String sendResetPassword(@RequestBody SendResetPasswordBody body) {
        emailConfirmationService.sendResetPassword(body);
        return "Sent";
    }

    @PutMapping(value = "ufd/user-service/confirmEmail")
    public String confirmEmail(@RequestParam(name = "token") String token) {
        emailConfirmationService.confirmEmail(token);
        return "Confirmed";
    }

    @PostMapping(value = "ufd/user-service/resetPassword")
    public String resetPassword(
            @RequestParam(name = "token") String token,
            @RequestBody ResetPasswordBody body) {
        emailConfirmationService.resetPassword(token, body);
        return "Password set";
    }
}
