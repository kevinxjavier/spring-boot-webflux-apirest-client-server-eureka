package com.kevinpina.controllers;

import com.kevinpina.models.documents.Product;
import com.kevinpina.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Value("${config.upload.path}")
    private String path;


    @GetMapping()// "/v1"
    public Flux<Product> listV1() {
        return productService.findAll();
    }

    @GetMapping("/v2")
    public Mono<ResponseEntity<Flux<Product>>> listV2() {
        return Mono.just(ResponseEntity.ok(productService.findAll()));
    }

    @GetMapping("/v3")
    public Mono<ResponseEntity<Flux<Product>>> listV3() {
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.findAll())
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> getById(@PathVariable String id) {
        return productService.findById(id)
                .map(p-> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping() // "/v1"
    public  Mono<ResponseEntity<Map<String, Object>>> create(@Valid @RequestBody Mono<Product> productMono) {

        Map<String, Object> response = new HashMap<>();

        return productMono.flatMap(product -> {
            if (product.getCreateAt() == null) {
                product.setCreateAt(new Date());
            }

            return productService.save(product).map(p -> {

                    response.put("product", p);
                    response.put("message", "Product created successful");
                    response.put("timestamp", new Date());

                    return ResponseEntity.created(URI.create("/api/product/".concat(p.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response);
            });
        }).onErrorResume(throwable -> {
            return Mono.just(throwable).cast(WebExchangeBindException.class)
                    .flatMap(exception -> Mono.just(exception.getFieldErrors()))

                    .flatMapMany(Flux::fromIterable)    // .flatMapMany(errors -> Flux.fromIterable(errors))
                    .map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                    .collectList()
                    .flatMap(list -> {

                        response.put("errors", list);
                        response.put("status", HttpStatus.BAD_REQUEST.value());
                        response.put("timestamp", new Date());

                        return Mono.just(ResponseEntity.badRequest().body(response));
                    });
        });

    }

    @PostMapping("/v2")
    public  Mono<ResponseEntity<Product>> createWithPicture(Product product, @RequestPart FilePart file) {
        if (product.getCreateAt() == null) {
            product.setCreateAt(new Date());
        }

        product.setPicture(UUID.randomUUID() + "-" +
                file.filename()
                        .replace(" ", "")
                        .replace(":", "")
                        .replace("\\", ""));

        return file.transferTo(new File(path + product.getPicture()))
                .then(productService.save(product)).map(p ->
                        ResponseEntity.created(URI.create("/api/product/".concat(p.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(p));
    }

    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<Product>> upload(@PathVariable String id, @RequestPart FilePart file) {
        return productService.findById(id)
                .flatMap(p -> {
                    p.setPicture(UUID.randomUUID() + "-" +
                            file.filename()
                                    .replace(" ", "")
                                    .replace(":", "")
                                    .replace("\\", ""));
                    return file.transferTo(new File(path + p.getPicture()))
                            .then(productService.save(p));
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public  Mono<ResponseEntity<Product>> update(@PathVariable String id, @RequestBody Product product) {

        return productService.findById(id).flatMap(p -> {
            // Only update what we need.
            p.setName(product.getName());
            p.setPrice(product.getPrice());
            p.setCategory(product.getCategory());
            return productService.save(p);
        })
                .map(p -> ResponseEntity.created(URI.create("/api/product/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return productService.findById(id)
                .flatMap(p -> productService.delete(p)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }

}
