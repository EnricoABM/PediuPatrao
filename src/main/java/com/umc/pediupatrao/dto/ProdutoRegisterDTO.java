package com.umc.pediupatrao.dto;

import com.umc.pediupatrao.entity.Produto;

public record ProdutoRegisterDTO(
        String id,
        String nome,
        String tipo,
        Double preco,
        Boolean ativo) {
    public Produto toModel() {
        Produto prod = new Produto();

        prod.setId(id);
        prod.setNome(nome);
        prod.setTipo(tipo);
        prod.setPreco(preco);
        prod.setAtivo(ativo);

        return prod;
    }
}
