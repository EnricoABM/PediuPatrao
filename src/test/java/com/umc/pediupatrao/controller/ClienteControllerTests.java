package com.umc.pediupatrao.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.service.ClienteService;

@WebMvcTest(ClienteController.class)
public class ClienteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @Test
    @WithMockUser(authorities = "CLIENT_READ_ALL")
    void deveListarClientesComSucesso() throws Exception {
        // Arrange
        Cliente cli = getClientTest();
        when(clienteService.listarClientes()).thenReturn(List.of(cli));

        // Act e Assert
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("content", "clientes/lista :: content"))
                .andExpect(model().attributeExists("clientes"));
    }

    @Test
    @WithMockUser(authorities = "CLIENT_CREATE")
    void deveExibirFormularioNovoCliente() throws Exception {
        // Act e Assert
        mockMvc.perform(get("/clientes/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("content", "clientes/form :: content"))
                .andExpect(model().attributeExists("cliente"));
    }

    @Test
    @WithMockUser(authorities = "CLIENT_CREATE")
    void deveSalvarClienteComSucesso() throws Exception {
        // Arrange
        Cliente cli = getClientTest();
        when(clienteService.salvar(any(Cliente.class))).thenReturn(cli);

        // Act e Assert
        mockMvc.perform(post("/clientes/salvar")
                .with(csrf())
                .param("nome", cli.getNome())
                .param("telefone", cli.getTelefone())
                .param("logradouro", cli.getLogradouro())
                .param("numero", cli.getNumero()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes"))
                .andExpect(flash().attributeExists("sucesso"));

        verify(clienteService, times(1)).salvar(any(Cliente.class));
    }

    @Test
    @WithMockUser(authorities = "CLIENT_READ")
    void deveExibirFormularioEdicaoQuandoClienteExiste() throws Exception {
        // Arrange
        Cliente cli = getClientTest();
        when(clienteService.buscarPorId(cli.getId())).thenReturn(Optional.of(cli));

        // Act & Assert
        mockMvc.perform(get("/clientes/editar/" + cli.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("content", "clientes/form :: content"))
                .andExpect(model().attributeExists("cliente"));
    }

    @Test
    @WithMockUser(authorities = "CLIENT_READ")
    void deveRedirecionarEdicaoQuandoClienteNaoExiste() throws Exception {
        // Arrange
        Cliente cli = getClientTest();
        when(clienteService.buscarPorId(cli.getId())).thenReturn(Optional.empty());

        // Act e Assert
        mockMvc.perform(get("/clientes/editar/" + cli.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes"))
                .andExpect(flash().attributeExists("erro"));

    }

    @Test
    @WithMockUser(authorities = "CLIENT_UPDATE")
    void deveAtualizarClienteComSucesso() throws Exception {
        // Arrange
        Cliente clienteMock = getClientTest();
        when(clienteService.salvar(any(Cliente.class))).thenReturn(clienteMock);

        // Act e Assert
        mockMvc.perform(post("/clientes/editar/64a7f2b1e4b0c8a123456789")
                .with(csrf())
                .param("nome", "Carlos Atualizado"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes"))
                .andExpect(flash().attributeExists("sucesso"));

        verify(clienteService, times(1)).salvar(any(Cliente.class));
    }

    @Test
    @WithMockUser(authorities = "CLIENT_DELETE")
    void deveDeletarClienteComSucesso() throws Exception {
        // Arrange
        doNothing().when(clienteService).excluir("64a7f2b1e4b0c8a123456789");

        // Act e Assert
        mockMvc.perform(post("/clientes/deletar/64a7f2b1e4b0c8a123456789")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes"))
                .andExpect(flash().attributeExists("sucesso"));

        verify(clienteService, times(1)).excluir("64a7f2b1e4b0c8a123456789");
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
