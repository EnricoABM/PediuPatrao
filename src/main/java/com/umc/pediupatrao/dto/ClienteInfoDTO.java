package com.umc.pediupatrao.dto;

import com.umc.pediupatrao.entity.Cliente;

public record ClienteInfoDTO(
        String id,
        String nome,
        String telefone,
        String endereco) {
    public static ClienteInfoDTO fromModel(Cliente cliente) {
        return new ClienteInfoDTO(
            cliente.getId(),
            cliente.getNome(),
            cliente.getTelefone(),
            cliente.getEndereco()
        );
    }
}
