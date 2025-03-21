package com.example.carsharingservice.controller;

import com.example.carsharingservice.dto.role.RoleNameDto;
import com.example.carsharingservice.dto.user.UserRegistrationRequestDto;
import com.example.carsharingservice.dto.user.UserResponseDto;
import com.example.carsharingservice.exception.RegistrationException;
import com.example.carsharingservice.mapper.UserMapper;
import com.example.carsharingservice.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints of management users.")
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update user", description = "update user role")
    public UserResponseDto updateRoleUser(@PathVariable Long id, @RequestBody
            @Valid RoleNameDto requestDto
    ) throws RegistrationException {
        return userService.updateRole(id,requestDto);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get current user profile",
            description = "Get current user profile. Available for Manager and Customer roles")
    public UserResponseDto getCurrentUserInfo() {
        return userMapper.toUserResponse(userService.getMe());
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Update current user profile",
            description = "Update current user profile")
    public UserResponseDto updateCurrentUserInfo(
            @RequestBody @Valid UserRegistrationRequestDto
                    requestDto) {
        return userService.updateMe(requestDto);
    }
}
