package com.matrix.ecommerce.product.service;

import com.matrix.ecommerce.product.entity.Product;
import com.matrix.ecommerce.product.repository.ProductRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private static final String IMAGE_UPLOAD_DIR = "uploads/images/";

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }


    // add some mock data through postconstruct
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

    // implement circuit breaker for this method
    @CircuitBreaker(
            fallbackMethod = "fallbackGetAllProducts",
            name = "product-service"
    )
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
        // Fallback logic, e.g., return an mock list or a cached version of the products
/*
        return List.of(
                new Product(UUID.randomUUID(), UUID.randomUUID(),"Fallback Product 1","", 0.0, 0, List.of("DIWALI-SALE"), "")
        );
*/
        return List.of(
                new Product(UUID.randomUUID(), "TV", 100.0, 200),
                new Product(UUID.randomUUID(), "Mobile", 50.0, 150)
        );
    }

    public int checkProductStock(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found")).getStock();
    }

/*

    public Product addProductWithPdfAndImage(String name, Double price, Integer quantity, MultipartFile pdfFile, MultipartFile imageFile) throws IOException {
        String fullText = extractTextFromPdf(pdfFile);

        String description = extractDescription(fullText);
        List<String> currentOffers = extractCurrentOffers(fullText);
        String imagePath = saveImage(imageFile);

        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setDescription(description);
        product.setCurrentOffers(currentOffers);
        product.setImagePath(imagePath);

        return productRepository.save(product);
    }

    private String generateProductId() {
        return "PROD-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document).trim();
        }
    }

    private String extractDescription(String text) {
        String descriptionPattern = "(?<=description:)([\\s\\S]+?)(?=Offers:|Price:|$)";
        Pattern descriptionPatternObj = Pattern.compile(descriptionPattern, Pattern.CASE_INSENSITIVE);
        Matcher descriptionMatcher = descriptionPatternObj.matcher(text);

        if (descriptionMatcher.find()) {
            String rawDescription = descriptionMatcher.group(0).trim();
            return rawDescription.replaceAll("\\r?\\n", " ").replaceAll("\\s+", " ").trim();
        }
        return "";
    }

    private List<String> extractCurrentOffers(String text) {
        String offerPattern = "(?<=Offers:)([\\s\\S]+?)(?=Price:|$)";
        Pattern offerPatternObj = Pattern.compile(offerPattern, Pattern.CASE_INSENSITIVE);
        Matcher offerMatcher = offerPatternObj.matcher(text);

        if (offerMatcher.find()) {
            String offersText = offerMatcher.group(0).trim();

            // Normalize line breaks
            String[] lines = offersText.split("\\r?\\n");
            List<String> offers = new ArrayList<>();

            StringBuilder currentOffer = new StringBuilder();
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // If line starts with "-" or "•", treat it as a new offer
                if (line.startsWith("-") || line.startsWith("•")) {
                    if (currentOffer.length() > 0) {
                        offers.add(currentOffer.toString().trim());
                    }
                    currentOffer = new StringBuilder(line.replaceFirst("[-•]\\s*", ""));
                } else {
                    currentOffer.append(" ").append(line);
                }
            }
            if (currentOffer.length() > 0) {
                offers.add(currentOffer.toString().trim());
            }

            return offers;
        }
        return List.of();
    }

    private String saveImage(MultipartFile image) throws IOException {
        File directory = new File(IMAGE_UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalFileName = image.getOriginalFilename();
        Path path = Paths.get(IMAGE_UPLOAD_DIR + originalFileName);
        Files.write(path, image.getBytes());

        return path.toString();
    }
*/



}
