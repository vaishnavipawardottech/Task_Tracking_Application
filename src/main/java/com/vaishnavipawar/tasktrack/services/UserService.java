package com.vaishnavipawar.tasktrack.services;


import com.vaishnavipawar.tasktrack.entities.User;
import com.vaishnavipawar.tasktrack.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public User getMyProfile() {

        return currentUserService.getCurrentUser();
    }

    public User updateMyProfile(
            String name,
            String password
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        if (name != null && !name.isBlank()) {
            currentUser.setName(name);
        }

        if (password != null && !password.isBlank()) {
            currentUser.setPasswordHash(
                    passwordEncoder.encode(password)
            );
        }

        return userRepository.save(currentUser);
    }

    public List<User> getAllUsers() {

        requireSuperAdmin();

        return userRepository.findAll();
    }

    public void deactivateUser(Long id) {

        requireSuperAdmin();

        User user = getUser(id);

        user.setActive(false);

        userRepository.save(user);
    }

    public void activateUser(Long id) {

        requireSuperAdmin();

        User user = getUser(id);

        user.setActive(true);

        userRepository.save(user);
    }

    public void deleteUser(Long id) {

        requireSuperAdmin();

        User user = getUser(id);

        userRepository.delete(user);
    }

    public User updateUserRole(
            Long id,
            User.Role role
    ) {

        requireSuperAdmin();

        User user = getUser(id);

        if (role == null) {
            throw new RuntimeException("Role cannot be null");
        }

        if (role == User.Role.SUPER_ADMIN) {
            throw new RuntimeException(
                    "Cannot assign SUPER_ADMIN role"
            );
        }

        user.setRole(role);

        return userRepository.save(user);
    }

    private User getUser(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }

    private void requireSuperAdmin() {

        User currentUser =
                currentUserService.getCurrentUser();

        if (currentUser.getRole() != User.Role.SUPER_ADMIN) {

            throw new RuntimeException(
                    "Only SUPER_ADMIN can perform this operation"
            );
        }
    }
}
