package com.pruebatecnica.backend_productos.service;

import com.pruebatecnica.backend_productos.entity.ProductDetail;
import reactor.core.publisher.Flux;

/**
 * Servicio de nengocio para productos similares.
 */
public interface SimilarProductsService {

    /**
     * Consulta y agrega en paralelo los detalles de los productos similares.
     *
     * @param productId identificador del producto
     * @return productos similares completos
     */
    Flux<ProductDetail> getSimilarProducts(String productId);
}
