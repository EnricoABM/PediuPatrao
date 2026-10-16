package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.user.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    // Busca um usuário por ID
    @PreAuthorize("hasAuthority('USER_READ')")
    public Optional<Usuario> buscarPorId(String id) {
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
        usuario.setId(UUID.randomUUID().toString());
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); // Criptografa a senha

        Usuario salvo = usuarioRepository.save(usuario);

        Map<String, Object[]> campos = new HashMap<>();
        campos.put("username", new Object[] { null, salvo.getUsername() });
        auditoriaService.registrar("INCLUSAO", "USUARIO", salvo.getId(), campos, null);

    }

    // Deleta um usuário pelo ID
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public void deletarUsuario(String id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        usuarioRepository.deleteById(id);

        if (usuario != null) {
            Map<String, Object[]> campos = new HashMap<>();
            campos.put("username", new Object[] { usuario.getUsername(), null });
            auditoriaService.registrar("EXCLUSAO", "USUARIO", id, campos, null);
        }
    }

    // Lista todos os usuários
    @PreAuthorize("hasAuthority('USER_READ_ALL')")
    public Iterable<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Atualiza o usuário usuários
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public void atualizarUsuario(String id, Usuario usuarioAtualizado) {
        usuarioRepository.findById(id).ifPresent(usuarioExistente -> {
            usuarioExistente.setUsername(usuarioAtualizado.getUsername());
            usuarioExistente.setRole(usuarioAtualizado.getRole());

            // Só atualiza a senha se uma nova foi informada
            if (usuarioAtualizado.getPassword() != null && !usuarioAtualizado.getPassword().isBlank()) {
                usuarioExistente.setPassword(passwordEncoder.encode(usuarioAtualizado.getPassword()));
            }

            String usernameAntigo = usuarioExistente.getUsername();
            String novoUsername = usuarioAtualizado.getUsername();

            usuarioRepository.save(usuarioExistente);

            Map<String, Object[]> camposModificados = new HashMap<>();
            if (!Objects.equals(usernameAntigo, usuarioExistente.getUsername())) {
                camposModificados.put("username", new Object[] { usernameAntigo, usuarioExistente.getUsername() });
            }

            if (!camposModificados.isEmpty()) {
                auditoriaService.registrar("ALTERACAO", "USUARIO", id, camposModificados, null);
            }
        });
    }
}
