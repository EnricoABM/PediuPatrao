package com.umc.pediupatrao.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    private final AuditoriaService auditoriaService;

    ProdutoService(ProdutoRepository produtoRepository, AuditoriaService auditoriaService) {
        this.produtoRepository = produtoRepository;
        this.auditoriaService = auditoriaService;
    }

    @PreAuthorize("hasAuthority('PRODUCT_READ_ALL')")
    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    // Método para excluir produto
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    public void excluir(String id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado para remoção."));

        produtoRepository.deleteById(id);

        Map<String, Object[]> campos = new HashMap<>();
        campos.put("nome", new Object[] { produto.getNome(), null });

        auditoriaService.registrar("EXCLUSAO", "PRODUTO", id, campos, null);
    }

    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    public Optional<Produto> buscarPorId(String id) {
        return produtoRepository.findById(id);
    }

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
        Produto salvo = produtoRepository.save(produto);

        Map<String, Object[]> campos = new HashMap<>();
        campos.put("nome", new Object[]{null, salvo.getNome()});
        campos.put("preco", new Object[]{null, salvo.getPreco()});

        auditoriaService.registrar("INCLUSAO", "PRODUTO", salvo.getId(), campos, null);

        return salvo;
    }

    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    public Produto editarProduto(Produto produto) {
        Produto produtoAntigo = produtoRepository.findById(produto.getId())
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado para atualização."));

        Produto atualizado = produtoRepository.save(produto);

        Map<String, Object[]> camposModificados = new HashMap<>();

        if (!Objects.equals(produtoAntigo.getNome(), atualizado.getNome())) {
            camposModificados.put("nome", new Object[]{produtoAntigo.getNome(), atualizado.getNome()});
        }
        if (!Objects.equals(produtoAntigo.getPreco(), atualizado.getPreco())) {
            camposModificados.put("preco", new Object[]{produtoAntigo.getPreco(), atualizado.getPreco()});
        }

        if (!camposModificados.isEmpty()) {
            auditoriaService.registrar("ALTERACAO", "PRODUTO", atualizado.getId(), camposModificados, null);
        }
        return atualizado;
    }
}
