package com.kevinpina.handler;

import com.kevinpina.models.documents.Category;
import com.kevinpina.models.documents.Product;
import com.kevinpina.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

@Component
public class ProductHandler {

    @Autowired
    private ProductService productService;

    @Value("${config.upload.path}")
    private String path;

    @Autowired
    private Validator validator;

    @Autowired
    private Environment environment;

    public Mono<ServerResponse> list(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.findAll(), Product.class);
    }

    public Mono<ServerResponse> get(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        /*return productService.findById(id)
                .flatMap(p -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        //.body(BodyInserters.fromValue(p)));
                        .body(BodyInserters.fromObject(p)))
                .switchIfEmpty(ServerResponse.notFound().build());*/

        return productService.findById(id).flatMap(p -> {
            p.setHost(getIp() + ":" + environment.getProperty("server.port"));
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    //.body(BodyInserters.fromValue(p)));
                    .body(BodyInserters.fromObject(p));
        })
        .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        Mono<Product> product = serverRequest.bodyToMono(Product.class);

        // Without Validation
        /*return product.flatMap(p -> {
                    if (p.getCreateAt() == null) {
                        p.setCreateAt(new java.util.Date());
                    }
                    return productService.save(p);
                })
                .flatMap(p -> ServerResponse.created(URI.create("/api/v4/product/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromObject(p)));*/

        // With Validation of Product and Category
        return product.flatMap(p -> {
                Errors errors = new BeanPropertyBindingResult(p, Product.class.getName());
                validator.validate(p, errors);

                if (errors.hasErrors()) {
                    //return Mono.error(new InterruptedException(errors.toString()));
                    return Flux.fromIterable(errors.getFieldErrors())
                            .map(fieldError -> "The Field '" + fieldError.getField() + "' " + fieldError.getDefaultMessage())
                            .collectList()
                            .flatMap(list -> ServerResponse.badRequest().body(BodyInserters.fromObject(list)));

                } else {
                    if (p.getCreateAt() == null) {
                        p.setCreateAt(new Date());
                    }
                    return productService.save(p).flatMap(pDb -> ServerResponse.created(URI.create("/api/v4/product/".concat(pDb.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromObject(pDb)));
                }
        });
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Product> product = serverRequest.bodyToMono(Product.class);

        // Option 1
        return product.flatMap(p -> {
            return productService.findById(id).flatMap(productDb -> {
                    productDb.setName(p.getName());
                    productDb.setPrice(p.getPrice());
                    productDb.setCategory(p.getCategory());
                    return productService.save(productDb);
            });
        }).flatMap(p -> ServerResponse.created(URI.create("/api/v4/product/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromObject(p)))
                        .switchIfEmpty(ServerResponse.notFound().build());

        // Option 2
        /*Mono<Product> productDb = productService.findById(id);
        return productDb.zipWith(product, (pDb, p) -> {
            pDb.setName(p.getName());
            pDb.setPrice(p.getPrice());
            pDb.setCategory(p.getCategory());
            return pDb;
        }).flatMap(p -> ServerResponse.created(URI.create("/api/v4/product/".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.save(p), Product.class))
                .switchIfEmpty(ServerResponse.notFound().build());*/
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return productService.findById(id).flatMap(p -> {
            return productService.delete(p).then(ServerResponse.noContent().build());
        }).switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> upload(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return serverRequest.multipartData()
                .map(multipart -> multipart.toSingleValueMap().get("fileName"))
                .cast(FilePart.class)
                .flatMap(file -> productService.findById(id).flatMap(p -> {
                    p.setPicture(UUID.randomUUID() + "-" + file.filename()
                            .replace(" ", "-")
                            .replace(":", "")
                            .replace("\\", ""));
                    return file.transferTo(new File(path + p.getPicture())).then(productService.save(p));
                }))
                .flatMap(p -> ServerResponse.created(URI.create("/api/v4/product/upload/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromObject(p)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createAndUpload(ServerRequest serverRequest) {

        Mono<Product> product = serverRequest.multipartData().map(multipart -> {
            FormFieldPart name = (FormFieldPart) multipart.toSingleValueMap().get("name");
            FormFieldPart price = (FormFieldPart) multipart.toSingleValueMap().get("price");
            FormFieldPart categoryId = (FormFieldPart) multipart.toSingleValueMap().get("category.id");
            FormFieldPart categoryName = (FormFieldPart) multipart.toSingleValueMap().get("category.name");

            Category category = Category.builder().id(categoryId.value()).name(categoryName.value()).build();

            return Product.builder().name(name.value()).price(Float.parseFloat(price.value())).category(category).build();
        });

        return serverRequest.multipartData()
                .map(multipart -> multipart.toSingleValueMap().get("fileName"))
                .cast(FilePart.class)
                .flatMap(file -> product.flatMap(p -> {
                    p.setPicture(UUID.randomUUID() + "-" + file.filename()
                            .replace(" ", "-")
                            .replace(":", "")
                            .replace("\\", ""));
                    p.setCreateAt(new Date());

                    return file.transferTo(new File(path + p.getPicture())).then(productService.save(p));
                }))
                .flatMap(p -> ServerResponse.created(URI.create("/api/v4/product/create_and_upload/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromObject(p)));
    }

    private String getIp() {
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch(UnknownHostException e) {
            ip = null;
        }

        return ip;
    }

}
