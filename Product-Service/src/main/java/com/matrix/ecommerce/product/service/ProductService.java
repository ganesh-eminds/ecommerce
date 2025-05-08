package com.matrix.ecommerce.product.service;

import com.matrix.ecommerce.dtos.dto.product.ProductDetails;
import com.matrix.ecommerce.product.entity.Product;
import com.matrix.ecommerce.product.repository.ProductRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @PostConstruct
    public void init() {
        Product mobile = new Product();
        mobile.setName("Mobile");
        mobile.setPrice(100.0);
        mobile.setStock(100);
        productRepository.save(mobile);

        Product tv = new Product();
        tv.setName("TV");
        tv.setPrice(200.0);
        tv.setStock(200);
        productRepository.save(tv);
    }

    public void restoreProductStock(UUID productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    @CircuitBreaker(fallbackMethod = "fallbackGetAllProducts", name = "product-service")
    @Retry(name = "productServiceRetry", fallbackMethod = "fallbackGetAllProducts")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(UUID productId) {
        return productRepository.findById(productId).orElse(null);
    }

    public Product updateProduct(UUID productId, Product product) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());

        return productRepository.save(existingProduct);
    }

    public List<Product> fallbackGetAllProducts(Exception e) {
        return List.of(
                Product.builder().name("Fallback Mobile").price(100.0).stock(100).imageUrl("/imagePath").build(),
                Product.builder().name("Fallback TV").price(200.0).stock(200).imageUrl("/imagePath").build()
        );
    }

    public List<ProductDetails> checkProductStock(List<UUID> productId) {
        List<Product> products = productRepository.findAllById(productId);
        return products.stream()
                .map(product -> ProductDetails.builder()
                        .productId(product.getId())
                        .quantity(product.getStock())
                        .build())
                .toList();
    }

    public Product createProductFromPdf(MultipartFile file) throws IOException {
        // Convert MultipartFile to File
        File tempFile = convertMultipartFileToFile(file);

        // Extract text from the PDF
        String pdfText = extractTextFromPDF(tempFile);

        // Parse the product information from the extracted text
        Product product = parseProductFromText(pdfText);

        // Save the product into the database
        return productRepository.save(product);
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("temp", file.getOriginalFilename());
        file.transferTo(tempFile);
        return tempFile;
    }

    private String extractTextFromPDF(File pdfFile) throws IOException {
        PDDocument document = PDDocument.load(pdfFile);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return text;
    }

    private Product parseProductFromText(String pdfText) {
        String[] lines = pdfText.split("\n");
        String name = null, imageUrl = null;
        double price = 0;
        int stock = 0;

        for (String line : lines) {
            if (line.startsWith("Name:")) {
                name = line.substring(5).trim();
            } else if (line.startsWith("Price:")) {
                price = Double.parseDouble(line.substring(6).trim());
            } else if (line.startsWith("Stock:")) {
                stock = Integer.parseInt(line.substring(6).trim());
            } else if (line.startsWith("Image URL:")) {
                imageUrl = line.substring(10).trim();
            }
        }

        return Product.builder()
                .name(name)
                .price(price)
                .stock(stock)
                .imageUrl(imageUrl)
                .build();
    }
}
