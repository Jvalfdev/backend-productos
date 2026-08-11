package com.pruebatecnica.backend_productos.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuración para la integración con servicios externos.
 */
@Configuration
public class WebClientConfig {

    // Carga de variables de configuración
    @Value("${external.product-service.base-url:http://localhost:3001}")
    private String productServiceBaseUrl;

    @Value("${external.product-service.connect-timeout-ms:2000}")
    private int connectTimeoutMs;

    @Value("${external.product-service.read-timeout-ms:2000}")
    private int readTimeoutMs;

    /**
     * Crea y configura el bean de WebClient.
     *
     * @return instancia de WebClient configurada
     */
    @Bean
    public WebClient productWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .responseTimeout(Duration.ofMillis(readTimeoutMs))
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS)) // Al leer datos
                        .addHandlerLast(new WriteTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))); // Al escribir datos

        return WebClient.builder()
                .baseUrl(productServiceBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
