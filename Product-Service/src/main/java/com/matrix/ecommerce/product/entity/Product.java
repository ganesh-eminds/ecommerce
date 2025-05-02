package com.matrix.ecommerce.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private double price;
    private int stock;
    private String imageUrl;
}
