package com.umc.pediupatrao.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoInitConfig implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    MongoInitConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!mongoTemplate.collectionExists("clientes")) {
            mongoTemplate.createCollection("clientes");
        }
        if (!mongoTemplate.collectionExists("usuarios")) {
            mongoTemplate.createCollection("usuarios");
        }
        if (!mongoTemplate.collectionExists("produtos")) {
            mongoTemplate.createCollection("produtos");
        }
        // Pedidos
        if (!mongoTemplate.collectionExists("pedidos")) {
            mongoTemplate.createCollection("pedidos");
        }
        if (!mongoTemplate.collectionExists("pedido_historico")) {
            mongoTemplate.createCollection("pedido_historico");
        }
        if (!mongoTemplate.collectionExists("item_pedido")) {
            mongoTemplate.createCollection("item_pedido");
        }
    }
}
