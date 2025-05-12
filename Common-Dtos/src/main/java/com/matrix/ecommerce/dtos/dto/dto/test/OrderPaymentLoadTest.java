package com.matrix.ecommerce.dtos.dto.dto.test;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.concurrent.*;
import org.json.*;

public class OrderPaymentLoadTest {

    private static final String ORDER_API = "http://localhost:8082/api/order";
    private static final String PAYMENT_API = "http://localhost:8083/api/payment-orders/create";
    private static final String USER_ID = "11111111-1111-1111-1111-111111111111";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) throws InterruptedException {
        int requestCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(100);

        for (int i = 0; i < requestCount; i++) {
            int index = i;
            executor.submit(() -> {
                try {
                    String orderId = sendOrderRequest(index);
                    if (orderId != null) {
                        sendPaymentRequest(orderId);
                    }
                } catch (Exception e) {
                    System.err.println("Error in thread " + index + ": " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
    }

    private static String sendOrderRequest(int index) {
        try {
            String orderPayload = """
                {
                    "userId":"%s",
                    "products": [
                        {
                            "productId": "658184c1-0bd7-4a62-89ef-6db74d7921fa",
                            "quantity": 15,
                            "price": 100.0
                        },
                        {
                            "productId": "b9d5b332-ef9d-4097-a0f7-c7a6cfbc9ac4",
                            "quantity": 30,
                            "price": 200.0
                        }
                    ],
                    "paymentMethod": "CREDIT_CARD"
                }
                """.formatted(USER_ID);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ORDER_API))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(orderPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                JSONObject json = new JSONObject(response.body());
                String orderId = json.optString("id", null);
                System.out.println(index + " => Order Success: " + orderId);
                return orderId;
            } else {
                System.out.println(index + " => Order Failed: " + response.body());
            }
        } catch (Exception e) {
            System.err.println(index + " => Order Error: " + e.getMessage());
        }
        return null;
    }

    private static void sendPaymentRequest(String orderId) {
        try {
            String paymentPayload = """
                {
                    "orderId": "%s",
                    "userId":"%s",
                    "paymentStatus":"PENDING"
                }
                """.formatted(orderId, USER_ID);

            try {
                Thread.sleep(2000);
            } catch (Exception e){
                System.err.println("Error in sleep: " + e.getMessage());
            }


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PAYMENT_API))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(paymentPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println("Payment Success: " + orderId);
            } else {
                System.out.println("Payment Failed: " + orderId + " - " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Payment Error: " + e.getMessage());
        }
    }
}
