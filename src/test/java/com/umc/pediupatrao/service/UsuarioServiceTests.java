package com.umc.pediupatrao.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.umc.pediupatrao.entity.user.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTests {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveBuscarUsuarioPorIdComSucesso() {
        // Arrange
        Usuario usuario = getUsuarioTest();
        when(usuarioRepository.findById("user-123")).thenReturn(Optional.of(usuario));

        // Act e Assert
        Optional<Usuario> resultado = usuarioService.buscarPorId("user-123");

        assertTrue(resultado.isPresent());
        assertEquals("admin", resultado.get().getUsername());
        verify(usuarioRepository, times(1)).findById("user-123");
    }

    @Test
    void deveBuscarUsuarioPorUsernameComSucesso() {
        // Arrange
        Usuario usuario = getUsuarioTest();
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        // Act e Assert
        Optional<Usuario> resultado = usuarioService.buscarPorUsername("admin");

        assertTrue(resultado.isPresent());
        assertEquals("user-123", resultado.get().getId());
        verify(usuarioRepository, times(1)).findByUsername("admin");
    }

    @Test
    void deveSalvarUsuarioComSucessoECriptografarSenha() {
        // Arrange
        Usuario usuario = getUsuarioTest();
        usuario.setId(null);
        usuario.setPassword("123456");

        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act e Assert
        usuarioService.salvarUsuario(usuario);

        assertNotNull(usuario.getId());
        assertEquals("encoded-password", usuario.getPassword());
        verify(passwordEncoder, times(1)).encode("123456");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void deveDeletarUsuarioPorIdComSucesso() {
        // Arrange
        doNothing().when(usuarioRepository).deleteById("user-123");

        // Act e Assert
        usuarioService.deletarUsuario("user-123");

        verify(usuarioRepository, times(1)).deleteById("user-123");
    }

    @Test
    void deveListarTodosOsUsuariosComSucesso() {
        // Arrange
        Usuario usuario = getUsuarioTest();
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        // Act e Assert
        Iterable<Usuario> resultado = usuarioService.listarTodos();

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void deveAtualizarUsuarioComSenhaNovaComSucesso() {
        // Arrange
        Usuario usuarioExistente = getUsuarioTest();
        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setUsername("novo-admin");
        usuarioAtualizado.setPassword("nova-senha");

        when(usuarioRepository.findById("user-123")).thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.encode("nova-senha")).thenReturn("encoded-nova-senha");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        // Act e Assert
        usuarioService.atualizarUsuario("user-123", usuarioAtualizado);

        assertEquals("novo-admin", usuarioExistente.getUsername());
        assertEquals("encoded-nova-senha", usuarioExistente.getPassword());
        verify(usuarioRepository, times(1)).findById("user-123");
        verify(passwordEncoder, times(1)).encode("nova-senha");
        verify(usuarioRepository, times(1)).save(usuarioExistente);
    }

    @Test
    void deveAtualizarUsuarioSemAlterarSenhaQuandoSenhaForNulaOuBranca() {
        // Arrange
        Usuario usuarioExistente = getUsuarioTest();
        String senhaAntiga = usuarioExistente.getPassword();

        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setUsername("novo-admin");
        usuarioAtualizado.setPassword("");

        when(usuarioRepository.findById("user-123")).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        // Act e Assert
        usuarioService.atualizarUsuario("user-123", usuarioAtualizado);

        assertEquals("novo-admin", usuarioExistente.getUsername());
        assertEquals(senhaAntiga, usuarioExistente.getPassword());
        verify(usuarioRepository, times(1)).findById("user-123");
        verify(passwordEncoder, never()).encode(any());
        verify(usuarioRepository, times(1)).save(usuarioExistente);
    }

    private Usuario getUsuarioTest() {
        Usuario usuario = new Usuario();
        usuario.setId("user-123");
        usuario.setUsername("admin");
        usuario.setPassword("senha-antiga");
        return usuario;
    }
}
