package com.example.carsharingservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotBlank
        @Size(min = 8, max = 30)
        @Email
        String email,
        @NotBlank
        @Size(min = 8, max = 20)
        String password
) {
}
