package org.example.controller;

import org.example.model.User;
import org.example.repository.RoleRepository;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class UserController {

    private final RoleRepository roleRepository;
    private final UserService userService;

    public UserController(UserService userService,
                          RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepository.findAll());
        return "user-form";
    }

    @PostMapping
    public String saveUser(
            @ModelAttribute("user") User user,
            @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {

        if (roleIds != null) {
            user.setRoles(
                    new HashSet<>(roleRepository.findAllById(roleIds))
            );
        }

        userService.saveUser(user);

        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("roles", roleRepository.findAll());
        return "user-form";
    }

    @PostMapping("/update")
    public String updateUser(
            @ModelAttribute("user") User user,
            @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {

        if (roleIds != null) {
            user.setRoles(
                    new HashSet<>(roleRepository.findAllById(roleIds))
            );
        }

        userService.updateUser(user);

        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}