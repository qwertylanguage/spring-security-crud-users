package org.example.controller;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public String userPage(Authentication authentication, Model model) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username);

        model.addAttribute("user", user);

        return "user";
    }
}
