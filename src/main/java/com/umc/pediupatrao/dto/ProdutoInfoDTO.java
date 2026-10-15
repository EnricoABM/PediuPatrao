package com.umc.pediupatrao.dto;

import com.umc.pediupatrao.entity.Produto;

public record ProdutoInfoDTO(
        String id,
        String nome,
        String tipo,
        Double preco,
        Boolean ativo) {

            public static ProdutoInfoDTO from(Produto produto) {
                return new ProdutoInfoDTO(
                    produto.getId(),
                    produto.getNome(),
                    produto.getTipo(), 
                    produto.getPreco(), 
                    produto.getAtivo()
                );
            }
}
