package com.kevinpina.client;

import com.kevinpina.client.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ProductHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/api/client"), handler::list)
                .andRoute(RequestPredicates.GET("/api/client/{id}"), handler::get)
                .andRoute(RequestPredicates.POST("/api/client"), handler::create)
                .andRoute(RequestPredicates.PUT("/api/client/{id}"), handler::edit)
                .andRoute(RequestPredicates.DELETE("/api/client/{id}"), handler::delete)
                .andRoute(RequestPredicates.POST("/api/client/upload/{id}"), handler::upload);
    }

}
