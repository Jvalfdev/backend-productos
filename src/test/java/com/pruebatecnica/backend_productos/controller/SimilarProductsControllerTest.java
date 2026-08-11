package com.pruebatecnica.backend_productos.controller;

import com.pruebatecnica.backend_productos.entity.ProductDetail;
import com.pruebatecnica.backend_productos.exception.ProductNotFoundException;
import com.pruebatecnica.backend_productos.service.SimilarProductsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimilarProductsControllerTest {

    @Mock
    private SimilarProductsService similarProductsService;

    @InjectMocks
    private SimilarProductsController similarProductsController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(similarProductsController)
                .controllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /product/{productId}/similar debe responder 200 OK con la lista de productos")
    void getSimilarProducts_Success() {
        ProductDetail dress = ProductDetail.builder().id("2").name("Dress").price(19.99).availability(true).build();
        ProductDetail blazer = ProductDetail.builder().id("3").name("Blazer").price(29.99).availability(false).build();

        when(similarProductsService.getSimilarProducts("1")).thenReturn(Flux.just(dress, blazer));

        webTestClient.get()
                .uri("/product/1/similar")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("2")
                .jsonPath("$[0].name").isEqualTo("Dress")
                .jsonPath("$[0].price").isEqualTo(19.99)
                .jsonPath("$[0].availability").isEqualTo(true)
                .jsonPath("$[1].id").isEqualTo("3")
                .jsonPath("$[1].name").isEqualTo("Blazer")
                .jsonPath("$[1].price").isEqualTo(29.99)
                .jsonPath("$[1].availability").isEqualTo(false);
    }

    @Test
    @DisplayName("GET /product/{productId}/similar debe responder 404 ProblemDetail cuando no existe el producto")
    void getSimilarProducts_NotFound() {
        when(similarProductsService.getSimilarProducts("6"))
                .thenReturn(Flux.error(new ProductNotFoundException("Product not found with id: 6")));

        webTestClient.get()
                .uri("/product/6/similar")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/problem+json")
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.title").isEqualTo("Product Not Found")
                .jsonPath("$.detail").isEqualTo("Product not found with id: 6")
                .jsonPath("$.type").isEqualTo("https://api.ecommerce.com/errors/not-found")
                .jsonPath("$.timestamp").exists();
    }
}
