package com.matrix.ecommerce.dtos.dto.dto.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    @Column(unique = true, nullable = false)
    private UUID productId;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    // Updated this to be a List<String> to hold multiple offers
    @ElementCollection
    @CollectionTable(name = "product_offers", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "offer")
    private List<String> currentOffers; // List of offers
    private String imagePath;
}

