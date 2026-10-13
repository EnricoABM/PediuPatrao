package com.umc.pediupatrao.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.umc.pediupatrao.entity.user.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByUsername(String username);
}
