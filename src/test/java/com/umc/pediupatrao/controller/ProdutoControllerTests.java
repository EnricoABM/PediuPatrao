package com.umc.pediupatrao.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.service.ProdutoService;

@WebMvcTest(ProdutoController.class)
public class ProdutoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService produtoService;

    @Test
    @WithMockUser(authorities = "PRODUCT_READ_ALL")
    void deveListarProdutosComSucesso() throws Exception {
        // Arrange
        Produto prod = getProductTest();
        when(produtoService.listarProdutos()).thenReturn(List.of(prod));

        // Act e Assert
        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("content", "produtos/lista :: content"))
                .andExpect(model().attributeExists("produtos"));
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_CREATE")
    void deveExibirFormularioNovoProduto() throws Exception {
        // Act e Assert
        mockMvc.perform(get("/produtos/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("content", "produtos/form :: content"))
                .andExpect(model().attributeExists("produto"));
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_CREATE")
    void deveSalvarProdutoComSucesso() throws Exception {
        // Arrange
        Produto prod = getProductTest();
        when(produtoService.salvar(any(Produto.class))).thenReturn(prod);

        // Act e Assert
        mockMvc.perform(post("/produtos")
                .with(csrf())
                .param("nome", prod.getNome())
                .param("tipo", "Pizza")
                .param("preco", String.valueOf(prod.getPreco()))
                .param("ativo", String.valueOf(prod.getAtivo())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/produtos"))
                .andExpect(flash().attributeExists("sucesso"));

        verify(produtoService, times(1)).salvar(any(Produto.class));
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_READ")
    void deveExibirFormularioEdicaoQuandoProdutoExiste() throws Exception {
        // Arrange
        Produto prod = getProductTest();
        when(produtoService.buscarPorId(prod.getId())).thenReturn(Optional.of(prod));

        // Act e Assert
        mockMvc.perform(get("/produtos/editar/" + prod.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("content", "produtos/form :: content"))
                .andExpect(model().attributeExists("produto"));
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_READ")
    void deveRedirecionarEdicaoQuandoProdutoNaoExiste() throws Exception {
        // Arrange
        Produto prod = getProductTest();
        when(produtoService.buscarPorId(prod.getId())).thenReturn(Optional.empty());

        // Act e Assert
        mockMvc.perform(get("/produtos/editar/" + prod.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/produtos"))
                .andExpect(flash().attributeExists("erro"));
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_DELETE")
    void deveDeletarProdutoComSucesso() throws Exception {
        // Arrange
        String produtoId = "prod-123";
        doNothing().when(produtoService).excluir(produtoId);

        // Act e Assert
        mockMvc.perform(post("/produtos/deletar/" + produtoId)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/produtos"))
                .andExpect(flash().attributeExists("sucesso"));

        verify(produtoService, times(1)).excluir(produtoId);
    }

    private Produto getProductTest() {
        Produto prod = new Produto();
        prod.setId("prod-123");
        prod.setNome("Pizza Margherita");
        prod.setPreco(45.00);
        prod.setAtivo(true);
        return prod;
    }
}
