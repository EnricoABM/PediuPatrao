package com.umc.pediupatrao.dto;

import com.umc.pediupatrao.entity.user.UserRole;
import com.umc.pediupatrao.entity.user.Usuario;

/**
 * UsuarioInfoDTO
 */
public record UsuarioInfoDTO(
        String id,
        String username,
        UserRole role) {
    public static UsuarioInfoDTO from(Usuario usuario) {
        return new UsuarioInfoDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRole());
    }
}
