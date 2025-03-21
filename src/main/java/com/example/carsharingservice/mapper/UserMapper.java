package com.example.carsharingservice.mapper;

import com.example.carsharingservice.config.MapperConfig;
import com.example.carsharingservice.dto.user.UserRegistrationRequestDto;
import com.example.carsharingservice.dto.user.UserResponseDto;
import com.example.carsharingservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(UserRegistrationRequestDto createUserRequestDto);

    @Mapping(target = "password", ignore = true)
    void updateUserFromDto(UserRegistrationRequestDto requestDto, @MappingTarget User user);

}
