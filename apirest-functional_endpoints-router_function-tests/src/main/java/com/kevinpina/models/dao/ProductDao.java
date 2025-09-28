package com.kevinpina.models.dao;

import com.kevinpina.models.documents.Category;
import com.kevinpina.models.documents.Product;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ProductDao extends ReactiveMongoRepository<Product, String> {

    Mono<Product> findByName(String name); // Used for Tests

    @Query("{'name':  ?0}") // Used for Tests
    Mono<Product> getByName(String name);

}
