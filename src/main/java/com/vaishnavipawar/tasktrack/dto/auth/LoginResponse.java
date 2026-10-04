package com.vaishnavipawar.tasktrack.dto.auth;

import com.vaishnavipawar.tasktrack.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private User.Role role;
    private String name;
}
