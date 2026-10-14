package com.umc.pediupatrao.dto;

import com.umc.pediupatrao.entity.user.UserRole;
import com.umc.pediupatrao.entity.user.Usuario;

public record UsuarioRegisterDTO(
    String username,
    String password,
    UserRole role
) {
    public Usuario toModel() {
        Usuario user = new Usuario();

        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        return user;
    }
}
