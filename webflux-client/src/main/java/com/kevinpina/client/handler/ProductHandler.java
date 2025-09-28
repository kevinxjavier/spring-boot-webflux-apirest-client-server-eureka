package com.kevinpina.client.handler;

import com.kevinpina.client.models.Product;
import com.kevinpina.client.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProductHandler {

    @Autowired
    private ProductService productService;

    public Mono<ServerResponse> list(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.findAll(), Product.class);
    }

    public Mono<ServerResponse> get(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return productService.findById(id)
                .flatMap(p -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)

                        //.body(BodyInserters.fromObject(product))
                        //.syncBody(p)
                        .bodyValue(p)

                ).switchIfEmpty(ServerResponse.notFound().build())


                // Handling error not found. Adding Body Message to NotFound HttpStatus "JSON Response Personalized"
                .onErrorResume(error -> {

                    WebClientResponseException exception = (WebClientResponseException) error;

                    if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                        Map<String, Object> body = new HashMap<>();
                        body.put("error", "Product not found: ".concat(exception.getMessage()));
                        body.put("timestamp", new Date());
                        body.put("status", exception.getStatusCode().value());
                        //return ServerResponse.status(HttpStatus.NOT_FOUND).syncBody(body);
                        return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(body);
                    }
                    return Mono.error(exception);

                });
    }

    /*
     * Due to Category.java in Server project is field name @NotNull from Lombok throw an Exception if not present in Postman or Curl
     */
    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        Mono<Product> productMono = serverRequest.bodyToMono(Product.class);

        return productMono.flatMap(p -> {
            if (p.getCreateAt() == null) {
                p.setCreateAt(new Date());
            }
            return productService.save(p);
        }).flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(p.getId())))
                    .contentType(MediaType.APPLICATION_JSON)
                    //.body(BodyInserters.fromObject(p))
                    //.syncBody(p))
                    .bodyValue(p))

                // Handling error not found
                .onErrorResume(error -> {

                    WebClientResponseException exception = (WebClientResponseException) error;

                    if (exception.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                //.syncBody(exception.getResponseBodyAsString());
                                .bodyValue(exception.getResponseBodyAsString());
                    }
                    return Mono.error(exception);

                });
    }

    public Mono<ServerResponse> edit(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Product> productMono = serverRequest.bodyToMono(Product.class);

        // Option 1
        //return productMono.flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(id)))
        //        .contentType(MediaType.APPLICATION_JSON)
        //        .body(productService.update(p, id), Product.class));

        // Option 2

        return productMono
                .flatMap(p -> productService.update(p, id))
                .flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(p.getId())))
                    .contentType(MediaType.APPLICATION_JSON)
                    //.syncBody(p))
                    .bodyValue(p))

                // Handling error not found
                .onErrorResume(error -> {

                    WebClientResponseException exception = (WebClientResponseException) error;

                    if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return ServerResponse.notFound().build();
                    }
                    return Mono.error(exception);

                });
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return errorHandler(productService.delete(id).then(ServerResponse.noContent().build()));
    }

    public Mono<ServerResponse> upload(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return errorHandler(serverRequest.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap(file -> productService.upload(file, id))
                .flatMap(product -> ServerResponse.created(URI.create("/api/client/".concat(product.getId())))
                    .contentType(MediaType.APPLICATION_JSON)
                    //.syncBody(product)));
                    .bodyValue(product)));
    }

    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> serverResponseMono) {
        return serverResponseMono.onErrorResume(error -> {

            WebClientResponseException exception = (WebClientResponseException) error;

            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ServerResponse.notFound().build();
            }
            return Mono.error(exception);

        });
    }

}
