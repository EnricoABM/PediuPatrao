package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.user.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Busca um usuário por ID
    @PreAuthorize("hasAuthority('USER_READ')")
    public Optional<Usuario> buscarPorId(UUID id) {
        return usuarioRepository.findById(id);
    }

    // Busca um usuário por username
    @PreAuthorize("hasAuthority('USER_READ')")
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // Salva um novo usuário (criptografando a senha)
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public void salvarUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); // Criptografa a senha
        usuarioRepository.save(usuario);
    }

    // Deleta um usuário pelo ID
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public void deletarUsuario(UUID id) {
        usuarioRepository.deleteById(id);
    }

    // Lista todos os usuários
    @PreAuthorize("hasAuthority('USER_READ_ALL')")
    public Iterable<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Atualiza o usuário usuários
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public void atualizarUsuario(UUID id, Usuario usuarioAtualizado) {
        usuarioRepository.findById(id).ifPresent(usuarioExistente -> {
            usuarioExistente.setUsername(usuarioAtualizado.getUsername());
            usuarioExistente.setRole(usuarioAtualizado.getRole());

            // Só atualiza a senha se uma nova foi informada
            if (usuarioAtualizado.getPassword() != null && !usuarioAtualizado.getPassword().isBlank()) {
                usuarioExistente.setPassword(passwordEncoder.encode(usuarioAtualizado.getPassword()));
            }

            usuarioRepository.save(usuarioExistente);
        });
    }
}
