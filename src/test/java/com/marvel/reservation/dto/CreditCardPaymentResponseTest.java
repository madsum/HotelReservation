package com.marvel.reservation.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreditCardPaymentResponseTest {

    @Test
    void testDefaultConstructorAndSetters() {
        CreditCardPaymentResponse response = new CreditCardPaymentResponse();
        response.setStatus("CONFIRMED");

        assertThat(response.getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    void testEqualsAndHashCode() {
        CreditCardPaymentResponse r1 = new CreditCardPaymentResponse();
        r1.setStatus("PENDING");

        CreditCardPaymentResponse r2 = new CreditCardPaymentResponse();
        r2.setStatus("PENDING");

        assertThat(r1)
                .isEqualTo(r2)
                .hasSameHashCodeAs(r2);
    }

    @Test
    void testToString() {
        CreditCardPaymentResponse response = new CreditCardPaymentResponse();
        response.setStatus("FAILED");

        assertThat(response.toString())
                .contains("status=FAILED");
    }
}
