package com.pruebatecnica.backend_productos.service;

import com.pruebatecnica.backend_productos.entity.ProductDetail;
import com.pruebatecnica.backend_productos.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimilarProductsServiceImpl implements SimilarProductsService {

    private final ProductRepository productRepository;

    @Override
    public Flux<ProductDetail> getSimilarProducts(String productId) {
        log.info("Consultando productos similares para productId: {}", productId);

        return productRepository.getSimilarProductIds(productId)
                .flatMap(productRepository::getProductDetail);
    }
}
