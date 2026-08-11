package com.pruebatecnica.backend_productos.repository;

import com.pruebatecnica.backend_productos.entity.ProductDetail;
import com.pruebatecnica.backend_productos.exception.ProductNotFoundException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

class ProductRepositoryImplTest {

    private MockWebServer mockWebServer;
    private ProductRepositoryImpl productRepository;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        productRepository = new ProductRepositoryImpl(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("getSimilarProductIds debe emitir los IDs recibidos del servidor externo")
    void getSimilarProductIds_Success() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("[\"2\", \"3\", \"4\"]"));

        Flux<String> result = productRepository.getSimilarProductIds("1");

        StepVerifier.create(result)
                .expectNext("2", "3", "4")
                .verifyComplete();
    }

    @Test
    @DisplayName("getSimilarProductIds debe emitir ProductNotFoundException en caso de 404")
    void getSimilarProductIds_NotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Flux<String> result = productRepository.getSimilarProductIds("6");

        StepVerifier.create(result)
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("getProductDetail debe deserializar el ProductDetail correctamente")
    void getProductDetail_Success() {
        String json = """
                {
                    "id": "2",
                    "name": "Dress",
                    "price": 19.99,
                    "availability": true
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(json));

        Mono<ProductDetail> result = productRepository.getProductDetail("2");

        StepVerifier.create(result)
                .expectNextMatches(p -> p.getId().equals("2")
                        && p.getName().equals("Dress")
                        && p.getPrice() == 19.99
                        && p.getAvailability().equals(true))
                .verifyComplete();
    }

    @Test
    @DisplayName("getProductDetail debe devolver Mono.empty() cuando un producto individual da 404")
    void getProductDetail_NotFound_ReturnsEmptyMono() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404));

        Mono<ProductDetail> result = productRepository.getProductDetail("100");

        StepVerifier.create(result)
                .verifyComplete();
    }
}
