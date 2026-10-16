package com.umc.pediupatrao.dto;

import java.time.LocalDateTime;

import com.umc.pediupatrao.entity.pedido.Pedido;
import com.umc.pediupatrao.entity.pedido.PedidoStatus;

public record PedidoListInfoDTO(
    String id,
    String clienteNome,
    String usuarioNome,
    PedidoStatus status,
    LocalDateTime horario
) {
    public static PedidoListInfoDTO from(Pedido pedido) {
        return new PedidoListInfoDTO(
            pedido.getId(), 
            pedido.getCliente().getNome(), 
            pedido.getUsuario().getUsername(),
            pedido.getStatus(),
            pedido.getHorario()
        );
    }
}
