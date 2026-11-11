package com.umc.pediupatrao.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.umc.pediupatrao.entity.pedido.Pedido;
import com.umc.pediupatrao.entity.pedido.PedidoHistorico;

@Repository
public class DashboardRepository {
    private final MongoTemplate mongoTemplate;

    public DashboardRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Pedido> pedidosCriadosEntre(LocalDateTime inicio, LocalDateTime fim) {
        Query query = new Query(Criteria.where("horario").gte(inicio).lt(fim));
        return mongoTemplate.find(query, Pedido.class);
    }

    public List<PedidoHistorico> historicosAte(LocalDateTime fim) {
        Query query = new Query(Criteria.where("horario").lt(fim))
                .with(Sort.by(Sort.Direction.ASC, "horario"));
        return mongoTemplate.find(query, PedidoHistorico.class);
    }
}
