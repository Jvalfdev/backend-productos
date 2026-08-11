package com.pruebatecnica.backend_productos.controller;

import com.pruebatecnica.backend_productos.entity.ProductDetail;
import com.pruebatecnica.backend_productos.service.SimilarProductsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Controlador REST para el endpoint de productos
 */
@Slf4j
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class SimilarProductsController {

    private final SimilarProductsService similarProductsService;

    /**
     * Endpoint GET /product/{productId}/similar -- para obtener productos similares.
     *
     * @param productId identificador del producto
     * @return lista de productos similares
     */
    @GetMapping(value = "/{productId}/similar", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ProductDetail>>> getSimilarProducts(@PathVariable String productId) {
        log.info("Petición recibida para obtener similares del productId: {}", productId);

        return similarProductsService.getSimilarProducts(productId)
                .collectList()
                .map(ResponseEntity::ok);
    }
}
