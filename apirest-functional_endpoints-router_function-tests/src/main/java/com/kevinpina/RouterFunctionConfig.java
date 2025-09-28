package com.kevinpina;

import com.kevinpina.handler.ProductHandler;
import com.kevinpina.models.documents.Product;
import com.kevinpina.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

    @Autowired
    private ProductService productService;

    /*@Bean
    public RouterFunction<ServerResponse> routes(ProductHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/api/product/v1"), handler::listProducts)
                .andRoute(GET("/api/product/v2"), handler::listProductsWithNamesUpperCase)
                .andRoute(GET("/api/product/v3"), handler::listProductsWithNamesUpperCaseAndRepeat)
                .andRoute(GET("/api/product/{id}"), handler::getProductById)
                .andRoute(POST("/api/product/v1"), handler::createProduct)
                .andRoute(POST("/api/product/v2"), handler::createProductWithPicture)
                .andRoute(PUT("/api/product/{id}"), handler::updateProduct)
                .andRoute(DELETE("/api/product/{id}"), handler::deleteProduct)
                .andRoute(POST("/api/product/upload/{id}"), handler::uploadPicture);
    }*/

    @Bean
    public RouterFunction<ServerResponse> routes1() {
        return route(GET("/api/v2/product/v1").or(GET("/api/test/product/v5")), request -> {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(productService.findAll(), Product.class);
        });
    }

    // Best solution decoupling the Handler from the configuration class RouterFunctionConfig
    @Bean
    public RouterFunction<ServerResponse> routes2(ProductHandler productHandler) {
        // return route(GET("/api/v2/product/v1").or(GET("/api/test/product/v5")), serverRequest -> productHandler.list(serverRequest));
        return route(GET("/api/v3/product").or(GET("/api/v3/products")), productHandler::list);
    }

    // Best solution decoupling the Handler from the configuration class RouterFunctionConfig
    @Bean
    public RouterFunction<ServerResponse> routes3(ProductHandler productHandler) {
        return route(GET("/api/v4/product").or(GET("/api/v4/products")), productHandler::list)
                .andRoute(GET("/api/v4/product/{id}").or(GET("/api/v4/product/get/{id}")), productHandler::get)
                .andRoute(POST("/api/v4/product"), productHandler::create)
                .andRoute(PUT("/api/v4/product/{id}"),productHandler::update)
                .andRoute(DELETE("/api/v4/product/{id}"), productHandler::delete)
                .andRoute(POST("/api/v4/product/upload/{id}"), productHandler::upload)
                .andRoute(POST("/api/v4/product/create_and_upload"), productHandler::createAndUpload);
    }

}
