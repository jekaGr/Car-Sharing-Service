package com.example.carsharingservice.dto.role;

import com.example.carsharingservice.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleNameDto {
    @NotNull
    @Schema(description = "User newRoleName",
            example = "MANAGER | CUSTOMER")
    private Role.RoleName roleName;
}
