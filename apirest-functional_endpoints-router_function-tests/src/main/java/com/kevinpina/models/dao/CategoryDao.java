package com.kevinpina.models.dao;

import com.kevinpina.models.documents.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CategoryDao extends ReactiveMongoRepository<Category, String> {

    Mono<Category> findByName(String name); // Used for Tests

}
