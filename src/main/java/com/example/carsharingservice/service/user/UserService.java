package com.example.carsharingservice.service.user;

import com.example.carsharingservice.dto.role.RoleNameDto;
import com.example.carsharingservice.dto.user.UserRegistrationRequestDto;
import com.example.carsharingservice.dto.user.UserResponseDto;
import com.example.carsharingservice.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);

    UserResponseDto updateRole(Long id, RoleNameDto requestDto);

    User getMe();

    UserResponseDto updateMe(UserRegistrationRequestDto requestDto);
}
