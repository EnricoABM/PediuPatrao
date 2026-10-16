package com.umc.pediupatrao.repository.pedido;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.umc.pediupatrao.entity.pedido.ItemPedido;

public interface ItemPedidoRepository extends MongoRepository<ItemPedido, String> {
    
}
