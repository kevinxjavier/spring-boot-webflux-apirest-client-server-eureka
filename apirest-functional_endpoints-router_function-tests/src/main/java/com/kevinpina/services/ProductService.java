package com.kevinpina.services;

import com.kevinpina.models.documents.Category;
import com.kevinpina.models.documents.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Flux<Product> findAll();
    Mono<Product> findById(String id);
    Flux<Product> findByNameUpperCase();
    Flux<Product> findByNameUpperCaseRepeat();
    Mono<Product> save(Product product);
    Mono<Void> delete(Product product);

    Flux<Category> finAllCategory();
    Mono<Category> findCategoryById(String id);
    Mono<Category> saveCategory(Category category);

    Mono<Product> findByName(String name); // Used by Tests
    Mono<Category> findCategoryByName(String name); // Used by Tests

}
