package com.pruebatecnica.backend_productos.exception;

/**
 * Excepción cuando un producto no existe.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
