package com.umc.pediupatrao.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTests {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void deveListarTodosOsProdutosComSucesso() {
        // Arrange
        Produto prod = getProductTest();
        when(produtoRepository.findAll()).thenReturn(Arrays.asList(prod));

        // Act e Assert
        List<Produto> resultado = produtoService.listarProdutos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Pizza Margherita", resultado.get(0).getNome());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarProdutoPorIdComSucesso() {
        // Arrange
        Produto prod = getProductTest();
        when(produtoRepository.findById("prod-123")).thenReturn(Optional.of(prod));

        // Act e Assert
        Optional<Produto> resultado = produtoService.buscarPorId("prod-123");

        assertTrue(resultado.isPresent());
        assertEquals("Pizza Margherita", resultado.get().getNome());
        verify(produtoRepository, times(1)).findById("prod-123");
    }

    @Test
    void deveExcluirProdutoPorIdComSucesso() {
        // Arrange
        doNothing().when(produtoRepository).deleteById("prod-123");

        // Act e Assert
        produtoService.excluir("prod-123");

        verify(produtoRepository, times(1)).deleteById("prod-123");
    }

    @Test
    void deveCriarNovoProdutoQuandoIdForNuloAoSalvar() {
        // Arrange
        Produto prod = getProductTest();
        prod.setId(null);
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0);
            assertNotNull(p.getId());
            return p;
        });

        // Act e Assert
        Produto resultado = produtoService.salvar(prod);

        assertNotNull(resultado);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void deveCriarNovoProdutoQuandoIdForVazioAoSalvar() {
        // Arrange
        Produto prod = getProductTest();
        prod.setId("");
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0);
            assertNotNull(p.getId());
            return p;
        });

        // Act e Assert
        Produto resultado = produtoService.salvar(prod);

        assertNotNull(resultado);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void deveAtualizarProdutoQuandoIdEstiverPreenchidoAoSalvar() {
        // Arrange
        Produto prod = getProductTest();
        prod.setId("prod-123");
        when(produtoRepository.existsById("prod-123")).thenReturn(true);
        when(produtoRepository.save(any(Produto.class))).thenReturn(prod);

        // Act e Assert
        Produto resultado = produtoService.salvar(prod);

        assertNotNull(resultado);
        verify(produtoRepository, times(1)).existsById("prod-123");
        verify(produtoRepository, times(1)).save(prod);
    }

    @Test
    void deveLancarExcecaoAoTentarAtualizarProdutoInexistente() {
        // Arrange
        Produto prod = getProductTest();
        prod.setId(null);
        when(produtoRepository.existsById(null)).thenReturn(false);

        // Act e Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.editarProduto(prod);
        });

        assertEquals("Cliente não encontrado para atualização.", exception.getMessage());
        verify(produtoRepository, times(1)).existsById(null);
        verify(produtoRepository, never()).save(any());
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
