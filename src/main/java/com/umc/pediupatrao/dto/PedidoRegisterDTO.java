package com.umc.pediupatrao.dto;

import java.util.List;

public record PedidoRegisterDTO(
    String clienteId,
    String usuarioId,
    List<ItemPedidoDTO> itens
) {
    
}
