package com.matrix.ecommerce.product.controller;

import com.matrix.ecommerce.dtos.dto.product.ProductDetails;
import com.matrix.ecommerce.product.entity.Product;
import com.matrix.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Create a new product", description = "Creates a new product. The product ID must be null.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Product ID should not be provided")
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        if (product.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @Operation(summary = "Get all products", description = "Fetches all products from the product catalog.")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get product by ID", description = "Fetch a product by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<Object> getProductById(
            @Parameter(description = "UUID of the product to be fetched")
            @PathVariable UUID productId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Update product", description = "Updates product details by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "UUID of the product to be updated")
            @PathVariable UUID productId,
            @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(productId, product);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Check stock availability", description = "Checks if the given product IDs are in stock.")
    @ApiResponse(responseCode = "200", description = "Stock availability returned")
    @GetMapping("/check-stock")
    public List<ProductDetails> checkProductStock(@RequestBody List<UUID> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }
        return productService.checkProductStock(productIds);
    }

    /*
    @Operation(summary = "Upload product with PDF and image", description = "Uploads a product with a PDF file and an image. Extracts PDF text into the description field and stores image path.")
    @ApiResponse(responseCode = "201", description = "Product uploaded successfully")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<Product> addProductWithPdfAndImage(
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("stock") Integer stock,
            @RequestParam("pdf") MultipartFile pdfFile,
            @RequestParam("image") MultipartFile imageFile) throws IOException {
        Product product = productService.addProductWithPdfAndImage(name, price, stock, pdfFile, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    */
}
