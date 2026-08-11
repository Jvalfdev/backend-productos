package com.pruebatecnica.backend_productos.service;

import com.pruebatecnica.backend_productos.entity.ProductDetail;
import com.pruebatecnica.backend_productos.exception.ProductNotFoundException;
import com.pruebatecnica.backend_productos.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimilarProductsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SimilarProductsServiceImpl similarProductsService;

    private ProductDetail dress;
    private ProductDetail blazer;
    private ProductDetail boots;

    @BeforeEach
    void setUp() {
        dress = ProductDetail.builder().id("2").name("Dress").price(19.99).availability(true).build();
        blazer = ProductDetail.builder().id("3").name("Blazer").price(29.99).availability(false).build();
        boots = ProductDetail.builder().id("4").name("Boots").price(39.99).availability(true).build();
    }

    @Test
    @DisplayName("Debe devolver la lista de productos similares agregados en paralelo")
    void getSimilarProducts_Success() {
        when(productRepository.getSimilarProductIds("1")).thenReturn(Flux.just("2", "3", "4"));
        when(productRepository.getProductDetail("2")).thenReturn(Mono.just(dress));
        when(productRepository.getProductDetail("3")).thenReturn(Mono.just(blazer));
        when(productRepository.getProductDetail("4")).thenReturn(Mono.just(boots));

        Flux<ProductDetail> result = similarProductsService.getSimilarProducts("1");

        StepVerifier.create(result)
                .expectNextMatches(p -> p.getId().equals("2") && p.getName().equals("Dress"))
                .expectNextMatches(p -> p.getId().equals("3") && p.getName().equals("Blazer"))
                .expectNextMatches(p -> p.getId().equals("4") && p.getName().equals("Boots"))
                .verifyComplete();

        verify(productRepository, times(1)).getSimilarProductIds("1");
        verify(productRepository, times(3)).getProductDetail(anyString());
    }

    @Test
    @DisplayName("Debe propagar ProductNotFoundException cuando el producto origen no existe")
    void getSimilarProducts_NotFound() {
        when(productRepository.getSimilarProductIds("6"))
                .thenReturn(Flux.error(new ProductNotFoundException("Product not found with id: 6")));

        Flux<ProductDetail> result = similarProductsService.getSimilarProducts("6");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ProductNotFoundException
                        && throwable.getMessage().contains("6"))
                .verify();

        verify(productRepository, times(1)).getSimilarProductIds("6");
        verify(productRepository, never()).getProductDetail(anyString());
    }

    @Test
    @DisplayName("Debe tolerar fallos en productos individuales devolviendo solo los válidos (Resiliencia)")
    void getSimilarProducts_ResilienceWithMissingIndividualProduct() {
        when(productRepository.getSimilarProductIds("2")).thenReturn(Flux.just("3", "100"));
        when(productRepository.getProductDetail("3")).thenReturn(Mono.just(blazer));
        when(productRepository.getProductDetail("100")).thenReturn(Mono.empty());

        Flux<ProductDetail> result = similarProductsService.getSimilarProducts("2");

        StepVerifier.create(result)
                .expectNextMatches(p -> p.getId().equals("3"))
                .verifyComplete();

        verify(productRepository, times(1)).getSimilarProductIds("2");
        verify(productRepository, times(2)).getProductDetail(anyString());
    }
}
