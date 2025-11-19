package com.meiorganizadinho.dto.userdto;

import com.meiorganizadinho.entity.Role;

import java.util.List;

public record RecoveryUserDto(
        Long id,
        String email,
        List<Role> roles
) {
}
