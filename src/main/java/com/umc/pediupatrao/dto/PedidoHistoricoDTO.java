package com.umc.pediupatrao.dto;

import java.time.LocalDateTime;

import com.umc.pediupatrao.entity.pedido.PedidoStatus;

public record PedidoHistoricoDTO(
    UsuarioInfoDTO usuario,
    LocalDateTime horario,
    PedidoStatus estadoAnterior,
    PedidoStatus estadoAtual
) {

}
