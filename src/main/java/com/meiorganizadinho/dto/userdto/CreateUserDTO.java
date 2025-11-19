package com.meiorganizadinho.dto.userdto;

import com.meiorganizadinho.enums.RoleName;

public record CreateUserDTO(
        String email,
        String password,
        RoleName role
) {
}
