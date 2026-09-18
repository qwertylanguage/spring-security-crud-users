package org.example.controller;

import org.example.dto.UserUpdateDto;
import org.example.model.Role;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import org.example.model.User;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.example.dto.UserResponseDto;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.example.dto.UserCreateDto;

import org.example.repository.RoleRepository;

@RestController
@RequestMapping("/api/admin/users")
public class AdminRestController {

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserCreateDto userDto) {

        User user = new User();

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setAge(userDto.getAge());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());

        if (userDto.getRoleIds() != null) {
            user.setRoles(
                    roleRepository.findAllById(userDto.getRoleIds())
                            .stream()
                            .collect(Collectors.toSet())
            );
        }

        userService.saveUser(user);

        return ResponseEntity.ok(convertToDto(user));
    }

    private UserResponseDto convertToDto(User user) {

        UserResponseDto dto = new UserResponseDto();

        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAge(user.getAge());
        dto.setUsername(user.getUsername());

        dto.setRoles(
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );

        return dto;
    }

    private final UserService userService;
    private final RoleRepository roleRepository;

    public AdminRestController(UserService userService,
                               RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {

        return userService.getAllUsers()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {

        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertToDto(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto userDto) {

        User existingUser = userService.getUserById(id);

        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setAge(userDto.getAge());
        existingUser.setUsername(userDto.getUsername());

        if (userDto.getRoleIds() != null) {
            existingUser.setRoles(
                    roleRepository.findAllById(userDto.getRoleIds())
                            .stream()
                            .collect(Collectors.toSet())
            );
        }

        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            existingUser.setPassword(userDto.getPassword());
        } else {
            existingUser.setPassword("");
        }

        userService.updateUser(existingUser);

        User updatedUser = userService.getUserById(id);

        return ResponseEntity.ok(convertToDto(updatedUser));
    }

}
