package com.umc.pediupatrao.repository.pedido;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.umc.pediupatrao.entity.pedido.Pedido;

import java.util.List;

public interface PedidoRepository extends MongoRepository<Pedido, String> {
    List<Pedido> findByClienteId(String clienteId);
}
