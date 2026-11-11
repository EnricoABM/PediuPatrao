package com.umc.pediupatrao.dto;

import com.umc.pediupatrao.entity.pedido.PedidoStatus;

public record TempoTransicaoDTO(PedidoStatus estadoAnterior, PedidoStatus estadoAtual,
        double mediaMinutos, long transicoes) {
}
