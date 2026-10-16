package com.umc.pediupatrao.repository.pedido;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.umc.pediupatrao.entity.pedido.PedidoHistorico;

public interface PedidoHistoricoRepository extends MongoRepository<PedidoHistorico, String> {
    
}
