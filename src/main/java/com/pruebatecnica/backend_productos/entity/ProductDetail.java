package com.pruebatecnica.backend_productos.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ProductDetail {
    private String id;
    private String name;
    private Double price;
    private Boolean availability;
}
