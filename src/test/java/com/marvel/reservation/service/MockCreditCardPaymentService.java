package com.marvel.reservation.service;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Primary
@Profile("test")
public class MockCreditCardPaymentService implements CreditCardPaymentService {

    @Override
    public boolean isPaymentConfirmed(String paymentReference) {
        // Mock logic: Assume payment is confirmed if the reference contains "SUCCESS"
        return paymentReference != null && paymentReference.contains("SUCCESS");
    }
}
