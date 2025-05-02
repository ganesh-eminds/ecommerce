package com.matrix.ecommerce.dtos.dto.payment;

public enum PaymentMethod {
    CREDIT_CARD("CREDIT_CARD"),
    DEBIT_CARD("DEBIT_CARD"),
    PAYPAL("PAYPAL"),
    CASH_ON_DELIVERY("CASH_ON_DELIVERY"),
    BANK_TRANSFER("BANK_TRANSFER");

    private final String method;

    PaymentMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
