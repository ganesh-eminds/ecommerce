package com.matrix.ecommerce.payment.service;

import com.matrix.ecommerce.dtos.dto.exception.ExceptionDto;
import com.matrix.ecommerce.dtos.dto.exception.ValidationException;
import com.matrix.ecommerce.payment.entity.Payment;
import com.matrix.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ValidationException(new ExceptionDto("PAYMENT_NOT_FOUND_WITH_PAYMENT_ID","404", "BAD REQUEST", "Payment Not Found with Payment Id: " + paymentId)));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment doPayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
