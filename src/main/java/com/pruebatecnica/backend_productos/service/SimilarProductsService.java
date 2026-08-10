package com.pruebatecnica.backend_productos.service;

import com.pruebatecnica.backend_productos.entity.ProductDetail;
import reactor.core.publisher.Flux;

public interface SimilarProductsService {
    Flux<ProductDetail> getSimilarProducts(String productId);
}
