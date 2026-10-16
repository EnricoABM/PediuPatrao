package com.umc.pediupatrao.dto;

import com.umc.pediupatrao.entity.pedido.ItemPedido;

public record ItemPedidoDTO(
        String produtoId,
        String produtoNome,
        Integer quantidade) {
    public static ItemPedidoDTO from(ItemPedido item) {
        return new ItemPedidoDTO(
                item.getId(),
                item.getProduto().getNome(),
                item.getQuantidade());
    }
}
