package org.example.config;

import org.example.model.Role;
import org.example.model.User;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Role userRole = roleRepository.findByName("ROLE_USER");

        if (userRole == null) {
            userRole = roleRepository.save(new Role("ROLE_USER"));
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN");

        if (adminRole == null) {
            adminRole = roleRepository.save(new Role("ROLE_ADMIN"));
        }

        String adminPassword = System.getenv("ADMIN_PASSWORD");

        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "Environment variable ADMIN_PASSWORD is not set"
            );
        }

        User admin = userRepository.findByUsername("admin");

        if (admin == null) {
            admin = new User();

            admin.setFirstName("Admin");
            admin.setLastName("Admin");
            admin.setAge(0);
            admin.setUsername("admin");

            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRoles(Set.of(userRole, adminRole));

            userRepository.save(admin);
        }
    }
}