package com.umc.pediupatrao.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @PreAuthorize("hasAuthority('PRODUCT_READ_ALL')")
    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    // Método para excluir produto
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    public void excluir(String id) {
        produtoRepository.deleteById(id);
    }

    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    public Optional<Produto> buscarPorId(String id) {
        return produtoRepository.findById(id);
    }

    // Método para salvar um novo produto ou atualizar um produto
    public Produto salvar(Produto produto) {
        if (produto.getId() == null || produto.getId().isBlank()) {
            return this.novoProduto(produto);
        } else {
            return this.editarProduto(produto);
        }
    }

    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    public Produto novoProduto(Produto produto) {
        produto.setId(UUID.randomUUID().toString());
        return produtoRepository.save(produto);
    }

    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    public Produto editarProduto(Produto produto) {
        if (produtoRepository.existsById(produto.getId())) {
            return produtoRepository.save(produto);
        } else {
            throw new IllegalArgumentException("Cliente não encontrado para atualização.");
        }
    }
}
