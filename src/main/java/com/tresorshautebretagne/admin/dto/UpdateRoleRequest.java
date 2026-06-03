package com.tresorshautebretagne.admin.dto;

import com.tresorshautebretagne.user.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @NotNull(message = "Le rôle est obligatoire")
    private Role role;
}
