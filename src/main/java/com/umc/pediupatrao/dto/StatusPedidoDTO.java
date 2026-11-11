package com.umc.pediupatrao.dto;

import com.umc.pediupatrao.entity.pedido.PedidoStatus;

public record StatusPedidoDTO(PedidoStatus status, long quantidade) {
}
