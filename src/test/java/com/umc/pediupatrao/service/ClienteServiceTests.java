package com.umc.pediupatrao.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.repository.ClienteRepository;
import com.umc.pediupatrao.utils.ClienteUtils;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTests {

    @Mock
    private ClienteUtils clienteUtils;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void deveCriarNovoClienteComSucesso() {
        // Arrange
        Cliente cli = this.getClientTest();

        when(clienteRepository.save(cli)).thenReturn(cli);
        // Act

        Cliente result = clienteService.novoCliente(cli);

        // Assert
        assertNotNull(result);
        verify(clienteUtils, times(1)).formatarEndereco(cli);
        verify(clienteRepository, times(1)).save(cli);
    }

    @Test
    void deveAtualizarClienteExistenteComSucesso() {
        // Arrange
        Cliente cli = this.getClientTest();

        when(clienteRepository.existsById(cli.getId())).thenReturn(true);
        when(clienteUtils.formatarEndereco(any(Cliente.class))).thenReturn("Rua B, 200");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cli);

        // Act
        Cliente resultado = clienteService.atualizar(cli);

        // Assert
        assertNotNull(resultado);
        assertEquals("Rua B, 200", cli.getEndereco());
        verify(clienteRepository, times(1)).existsById(cli.getId());
        verify(clienteUtils, times(1)).formatarEndereco(cli);
        verify(clienteRepository, times(1)).save(cli);
    }

    @Test 
    void deveLancarExcecaoAoTentarAtualizarClienteInexistente() {
        // Arrange
        Cliente cli = this.getClientTest();
        String id = cli.getId();

        when(clienteRepository.existsById(id)).thenReturn(false);

        // Act e Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.atualizar(cli);
        });

        assertEquals("Cliente não encontrado para atualização.", ex.getMessage());
        verify(clienteRepository, times(1)).existsById(id);
        verify(clienteRepository, never()).deleteById(any());
    }

    @Test
    void deveListarTodosOsClientes() {
        // Arrange
        List<Cliente> lista = List.of(new Cliente(), new Cliente());
        when(clienteRepository.findAll()).thenReturn(lista);

        // Act
        List<Cliente> resultado = clienteService.listarClientes();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(clienteRepository, times(1)).findAll();
    }

@Test
    void deveBuscarClientePorIdComSucesso() {
        // Arrange
        Cliente cli = this.getClientTest();

        when(clienteRepository.findById(cli.getId())).thenReturn(Optional.of(cli));

        // Act
        Optional<Cliente> resultado = clienteService.buscarPorId(cli.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(cli.getNome(), resultado.get().getNome());
        verify(clienteRepository, times(1)).findById(cli.getId());
    }

    @Test
    void deveExcluirClienteComSucesso() {
        // Arrange
        Cliente cli = this.getClientTest();
        String id = cli.getId();

        when(clienteRepository.existsById(id)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(id);

        // Act e Assert
        assertDoesNotThrow(() -> clienteService.excluir(id));

        verify(clienteRepository, times(1)).existsById(id);
        verify(clienteRepository, times(1)).deleteById(id);
    }

    @Test
    void deveLancarExcecaoAoTentarExcluirClienteInexistente() {
        // Arrange
        Cliente cli = this.getClientTest();
        String id = cli.getId();

        when(clienteRepository.existsById(id)).thenReturn(false);

        // Act e Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.excluir(id);
        });

        assertEquals("Cliente não encontrado para remoção.", ex.getMessage());
        verify(clienteRepository, times(1)).existsById(id);
        verify(clienteRepository, never()).deleteById(any());
    }

    @Test
    void deveChamarNovoClienteQuandoIdForNulo() {
        // Arrange
        Cliente cli = this.getClientTest();

        cli.setId(null);
        when(clienteUtils.formatarEndereco(any(Cliente.class))).thenReturn("Rua Nova, 123");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cli);

        // Act
        Cliente resultado = clienteService.salvar(cli);

        // Assert
        assertNotNull(resultado);
        verify(clienteUtils, times(1)).formatarEndereco(cli);
        verify(clienteRepository, times(1)).save(cli);
        verify(clienteRepository, never()).existsById(any());
    }

    @Test
    void deveChamarAtualizarQuandoIdNaoForNuloAoSalvar() {
        // Arrange
        Cliente cli = this.getClientTest();

        cli.setId("123");
        when(clienteRepository.existsById("123")).thenReturn(true);
        when(clienteUtils.formatarEndereco(any(Cliente.class))).thenReturn("Rua Atualizada, 456");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cli);

        // Act
        Cliente resultado = clienteService.salvar(cli);

        // Assert
        assertNotNull(resultado);
        verify(clienteRepository, times(1)).existsById("123");
        verify(clienteUtils, times(1)).formatarEndereco(cli);
        verify(clienteRepository, times(1)).save(cli);
    }

    private Cliente getClientTest() {
        Cliente cli = new Cliente();

        cli.setId("64a7f2b1e4b0c8a123456789");
        cli.setNome("Fulano da Silva");
        cli.setTelefone("(11) 91111-2222");

        cli.setCep("08780-900");
        cli.setLogradouro("Av. Vereador Narciso Yague Guimarães");
        cli.setNumero("277");
        cli.setComplemento(null);
        cli.setBairro("Centro Cívico");
        cli.setCidade("Mogi das Cruzes");
        cli.setEstado("SP");

        cli.setEndereco("Av. Vereador Narciso Yague Guimarães, 277, Centro Cívico, Mogi das Cruzes, SP");

        return cli;
    }
}
