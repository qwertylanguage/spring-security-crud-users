package org.example.service;

import org.example.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByUsername(String username);

    void saveUser(User user);

    void updateUser(User user);

    void deleteUser(Long id);
}