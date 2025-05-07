package com.matrix.ecommerce.product.controller;

import com.matrix.ecommerce.dtos.dto.product.ProductDetails;
import com.matrix.ecommerce.product.entity.Product;
import com.matrix.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
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
    public ResponseEntity<Object> getProductById(@PathVariable UUID productId) {
        // Validate the productId
        Product product = productService.getProductById(productId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable UUID productId, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(productId, product);
        return ResponseEntity.ok(updatedProduct);
    }
    // product stock availability check
    @PostMapping("/check-stock")
    public List<ProductDetails> checkProductStock(@RequestBody List<UUID> productIds) {
        // Validate the productIds
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }
        return productService.checkProductStock(productIds);
    }

    // filter products based on category
    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filterProducts(@RequestParam String category) {
        List<Product> products = productService.filterProducts(category);
        return ResponseEntity.ok(products);
    }
    // delete product by id
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
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
