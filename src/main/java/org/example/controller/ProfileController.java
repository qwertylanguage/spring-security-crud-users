package org.example.controller;

import org.example.model.User;
import org.example.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String userPage(Authentication authentication, Model model) {

        String username = authentication.getName();

        User user = userService.getUserByUsername(username);

        model.addAttribute("user", user);

        return "user";
    }
}