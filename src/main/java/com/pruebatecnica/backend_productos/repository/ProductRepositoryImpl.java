package com.pruebatecnica.backend_productos.repository;

import com.pruebatecnica.backend_productos.entity.ProductDetail;
import com.pruebatecnica.backend_productos.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementación del repositorio de productos
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final WebClient productWebClient;

    @Override
    public Flux<String> getSimilarProductIds(String productId) {
        return productWebClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
                        response -> Mono.error(new ProductNotFoundException("Product not found with id: " + productId)))
                .onStatus(HttpStatusCode::isError,
                        response -> Mono.error(new RuntimeException("External error with status: " + response.statusCode())))
                .bodyToMono(String[].class)
                .flatMapMany(Flux::fromArray);
    }

    @Override
    public Mono<ProductDetail> getProductDetail(String productId) {
        return productWebClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
                        response -> {
                            log.warn("Similar product detail not found for id: {}", productId);
                            return Mono.empty();
                        })
                .bodyToMono(ProductDetail.class)
                .onErrorResume(e -> {
                    log.error("Failed to fetch product detail for id: {}. Skipping.", productId, e);
                    return Mono.empty();
                });
    }
}
