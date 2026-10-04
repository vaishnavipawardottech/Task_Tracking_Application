package com.vaishnavipawar.tasktrack.services;

import com.vaishnavipawar.tasktrack.entities.User;
import com.vaishnavipawar.tasktrack.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public User register(
            String name,
            String email,
            String password,
            User.Role role
    ) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User.Role finalRole =
                role == null ? User.Role.MEMBER : role;

        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(password)
        );
        user.setRole(finalRole);
        user.setActive(true);

        return userRepository.save(user);
    }

    public LoginResponse login(
            String email,
            String password
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );

        if (!user.isActive()) {
            throw new RuntimeException("User account is inactive");
        }

        if (!passwordEncoder.matches(
                password,
                user.getPasswordHash()
        )) {
            throw new RuntimeException("Invalid email or password");
        }

        /*
         * JWT generation will be added here
         * when JwtService is implemented.
         */

        String token = "JWT_TOKEN_WILL_BE_GENERATED_HERE";

        return new LoginResponse(
                token,
                user.getRole(),
                user.getName()
        );
    }

    public void logout() {

        /*
         * JWT blacklist logic will be implemented here.
         *
         * Options:
         * 1. In-memory blacklist
         * 2. Database token blacklist
         */
    }

    public record LoginResponse(
            String token,
            User.Role role,
            String name
    ) {
    }
}
