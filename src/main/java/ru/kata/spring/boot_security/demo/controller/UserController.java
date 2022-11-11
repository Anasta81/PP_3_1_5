package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserServiceDetailsImp;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserServiceDetailsImp userServiceImp;

    @Autowired
    public UserController(UserServiceDetailsImp userServiceImp) {
        this.userServiceImp = userServiceImp;
    }

    @GetMapping
    public String welcomeUser(Principal principal, Model model){
        User user = (User) userServiceImp.loadUserByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user";
    }
}
