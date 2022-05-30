package com.smoothstack.usermicroservice.controller;

import com.smoothstack.common.models.User;
import com.smoothstack.usermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    UserService userService;

    @GetMapping("/u/{username}")
    public User getUserLoginInfo(@PathVariable(name = "username") String username) {
        return userService.getLoginInfo(username);
    }
}
