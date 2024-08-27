package com.ramongibson.lockboxapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 14, message = "Username must be between 3 and 14 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 14, message = "Password must be between 8 and 14 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,14}$",
            message = "Password must contain at least one number, one special character, and be between 8 and 14 characters"
    )
    private String password;
}