package com.example.carsharingservice.util.user;

import com.example.carsharingservice.dto.user.UserRegistrationRequestDto;
import com.example.carsharingservice.dto.user.UserResponseDto;
import com.example.carsharingservice.model.Role;
import com.example.carsharingservice.model.User;
import java.util.Set;

public class TestUserUtil {
    public static User getUser() {
        Role role = new Role();
        role.setName(Role.RoleName.CUSTOMER);
        return new User().setId(3L).setEmail("email@email.com").setPassword("password")
                .setFirstName("firstName").setLastName("lastName").setRoles(Set.of(role));
    }

    public static UserRegistrationRequestDto getUserRegistrationRequestDto() {
        return new UserRegistrationRequestDto().setEmail("email@email.com").setPassword("password")
                .setFirstName("firstName").setLastName("lastName").setRepeatPassword("password");
    }

    public static UserResponseDto getUserResponseDto() {
        return new UserResponseDto().setId(3L).setEmail("email@email.com")
                .setFirstName("firstName").setLastName("lastName");
    }
}
