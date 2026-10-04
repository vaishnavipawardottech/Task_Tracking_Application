package com.vaishnavipawar.tasktrack.controllers;

import com.vaishnavipawar.tasktrack.entities.User;
import com.vaishnavipawar.tasktrack.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile() {

        return ResponseEntity.ok(
                userService.getMyProfile()
        );
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(
            @RequestBody UpdateProfileRequest request
    ) {

        return ResponseEntity.ok(
                userService.updateMyProfile(
                        request.name(),
                        request.password()
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(
            @PathVariable Long id
    ) {

        userService.deactivateUser(id);

        return ResponseEntity.ok("User deactivated successfully");
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<String> activateUser(
            @PathVariable Long id
    ) {

        userService.activateUser(id);

        return ResponseEntity.ok("User activated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id
    ) {

        userService.deleteUser(id);

        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<User> updateUserRole(
            @PathVariable Long id,
            @RequestBody UpdateRoleRequest request
    ) {

        return ResponseEntity.ok(
                userService.updateUserRole(
                        id,
                        request.role()
                )
        );
    }

    public record UpdateProfileRequest(
            String name,
            String password
    ) {
    }

    public record UpdateRoleRequest(
            User.Role role
    ) {
    }

}
