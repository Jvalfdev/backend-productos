package com.pruebatecnica.backend_productos.repository;

import com.pruebatecnica.backend_productos.entity.ProductDetail;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Flux<String> getSimilarProductIds(String productId);
    Mono<ProductDetail> getProductDetail(String productId);
}
