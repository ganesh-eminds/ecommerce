package com.matrix.ecommerce.product.controller;

import com.matrix.ecommerce.product.entity.Product;
import com.matrix.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // throw error if product has id
        if (product.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    // get all products
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable UUID productId, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(productId, product);
        return ResponseEntity.ok(updatedProduct);
    }
/*
    @Operation(summary = "Upload product with PDF and image", description = "Uploads a product with a PDF file and an image. Extracts PDF text into the description field and stores image path.")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<Product> addProductWithPdfAndImage(@RequestParam("name") String name, @RequestParam("price") Double price, @RequestParam("stock") Integer stock, @RequestParam("pdf") MultipartFile pdfFile, @RequestParam("image") MultipartFile imageFile) throws IOException {
        // Handle file uploads and save the product with PDF and image
        Product product = productService.addProductWithPdfAndImage(name, price, stock, pdfFile, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }*/
}
