package com.kevinpina.services;

import com.kevinpina.models.dao.CategoryDao;
import com.kevinpina.models.dao.ProductDao;
import com.kevinpina.models.documents.Category;
import com.kevinpina.models.documents.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<Product> findAll() {
        return productDao.findAll();
    }

    @Override
    public Mono<Product> findById(String id) {
        return productDao.findById(id);
    }

    @Override
    public Flux<Product> findByNameUpperCase() {
        Collation collation = Collation.of("en");
        Query query = new Query().collation(collation);

        return mongoTemplate.find(query, Product.class)
                .map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                });
    }

    @Override
    public Flux<Product> findByNameUpperCaseRepeat() {
        return findByNameUpperCase().repeat(5000);
    }

    @Override
    public Mono<Product> save(Product product) {
        return productDao.save(product);
    }

    @Override
    public Mono<Void> delete(Product product) {
        return productDao.delete(product);
    }

    @Override
    public Flux<Category> finAllCategory() {
        return categoryDao.findAll();
    }

    @Override
    public Mono<Category> findCategoryById(String id) {
        return categoryDao.findById(id);
    }

    @Override
    public Mono<Category> saveCategory(Category category) {
        return categoryDao.save(category);
    }

    @Override
    public Mono<Product> findByName(String name) {
        // return productDao.getByName(name);
        return productDao.findByName(name);
    }

    @Override
    public Mono<Category> findCategoryByName(String name) {
        return categoryDao.findByName(name);
    }

}
