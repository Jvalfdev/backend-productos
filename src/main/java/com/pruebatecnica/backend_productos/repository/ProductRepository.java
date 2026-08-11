package com.pruebatecnica.backend_productos.repository;

import com.pruebatecnica.backend_productos.entity.ProductDetail;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Contrato de acceso a datos para consultar el catálogo externo de productos.
 */
public interface ProductRepository {

    /**
     * Obtiene los IDs de los productos similares a un producto dado.
     *
     * @param productId identificador del producto origen
     * @return IDs de productos similares
     */
    Flux<String> getSimilarProductIds(String productId);

    /**
     * Obtiene el detalle completo de un producto individual.
     *
     * @param productId identificador del producto
     * @return Mono con el detalle del producto o Mono.empty() si no se encuentra el producto
     */
    Mono<ProductDetail> getProductDetail(String productId);
}
