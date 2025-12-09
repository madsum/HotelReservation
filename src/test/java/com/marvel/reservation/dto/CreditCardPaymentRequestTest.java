package com.marvel.reservation.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreditCardPaymentRequestTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        CreditCardPaymentRequest req = new CreditCardPaymentRequest();
        req.setPaymentReference("ABC123");

        assertThat(req.getPaymentReference()).isEqualTo("ABC123");
    }

    @Test
    void testAllArgsConstructor() {
        CreditCardPaymentRequest req = new CreditCardPaymentRequest("REF-999");

        assertThat(req.getPaymentReference()).isEqualTo("REF-999");
    }

    @Test
    void testEqualsAndHashCode() {
        CreditCardPaymentRequest req1 = new CreditCardPaymentRequest("REF-1");
        CreditCardPaymentRequest req2 = new CreditCardPaymentRequest("REF-1");

        assertThat(req1)
                .isEqualTo(req2)
                .hasSameHashCodeAs(req2);
    }

    @Test
    void testToString() {
        CreditCardPaymentRequest req = new CreditCardPaymentRequest("TEST-REF");

        assertThat(req.toString())
                .contains("paymentReference=TEST-REF");
    }
}
