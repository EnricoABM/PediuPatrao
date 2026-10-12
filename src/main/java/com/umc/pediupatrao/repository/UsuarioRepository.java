package com.umc.pediupatrao.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.umc.pediupatrao.entity.user.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends MongoRepository<Usuario, UUID> {
    Optional<Usuario> findByUsername(String username);
}
