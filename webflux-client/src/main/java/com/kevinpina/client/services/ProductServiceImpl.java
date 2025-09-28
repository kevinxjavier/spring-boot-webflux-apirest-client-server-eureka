package com.kevinpina.client.services;

import com.kevinpina.client.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private WebClient.Builder webClient;

    @Override
    public Flux<Product> findAll() {
        return webClient.build().get()
                .accept(MediaType.APPLICATION_JSON)

                // Option 1 "Deprecated"
                //.exchange()
                //.flatMapMany(response -> response.bodyToFlux(Product.class));

                // Option 2
                .retrieve()
                .bodyToFlux(Product.class);
    }

    @Override
    public Mono<Product> findById(String id) {
        Map<String, Object> params = Map.of("id", id);

        return webClient.build().get().uri("/{id}", params)
                .accept(MediaType.APPLICATION_JSON)

                // Option 1 "Deprecated"
                //.exchange()
                //.flatMap(response -> response.bodyToMono(Product.class));

                // Option 2
                .retrieve()
                .bodyToMono(Product.class);
    }

    @Override
    public Mono<Product> save(Product product) {
        return webClient.build().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)

                // Option 1 "Deprecated"
                .body(BodyInserters.fromObject(product))

                // Option 2 "Deprecated"
                //.syncBody(product)

                .retrieve()
                .bodyToMono(Product.class);
    }

    @Override
    public Mono<Product> update(Product product, String id) {
        return webClient.build().put()
                .uri("/{id}", Collections.singletonMap("id", id))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)

                // Option 1 "Deprecated"
                //.body(BodyInserters.fromObject(product))

                // Option 2 "Deprecated"
                //.syncBody(product)

                // Option 3
                .bodyValue(product)

                .retrieve()
                .bodyToMono(Product.class);
    }

    @Override
    public Mono<Void> delete(String id) {
        return webClient.build().delete().uri("/{id}", Collections.singletonMap("id", id))

                // Option 1
                //.exchange()
                //.then();

                // Option 2
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Product> upload(FilePart filePart, String id) {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.asyncPart("file", filePart.content(), DataBuffer.class) // Postman or Curl variable name "file" in this project
                .headers(header -> {
                    header.setContentDispositionFormData("fileName", filePart.filename()); // Postman or Curl variable name "fileName" in project: "spring-boot-webflux-apirest-functional_endpoints-router_function-tests"
                });

        return webClient.build().post().uri("/upload/{id}", Collections.singletonMap("id", id))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .syncBody(multipartBodyBuilder.build())
                .retrieve()
                .bodyToMono(Product.class);
    }

}
