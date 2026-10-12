package com.umc.pediupatrao.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.umc.pediupatrao.entity.Produto;

public interface ProdutoRepository extends MongoRepository<Produto, String> {
}
