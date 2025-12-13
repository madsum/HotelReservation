package com.marvel.reservation.service;

import com.marvel.reservation.constant.Message;
import com.marvel.reservation.dto.CreditCardPaymentRequest;
import com.marvel.reservation.dto.CreditCardPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditCardPaymentServiceImpl implements CreditCardPaymentService {
    private final String CARD_STATUS = "CONFIRMED";

    private final RestTemplate restTemplate;

    @Value("${credit.card.payment.service.url:http://localhost:8081/api/v1/payment-status}")
    private String paymentStatusUrl;

    @Override
    public boolean isPaymentConfirmed(String paymentReference) {
        // In a real application, I would use the OpenAPI spec to generate a client
        // and make a proper request. For this assignment, I will simulate the request
        // based on the provided spec.
        // The spec suggests a POST request to /payment-status with a request body

        CreditCardPaymentRequest request = new CreditCardPaymentRequest(paymentReference);

        try {
            CreditCardPaymentResponse response = restTemplate.postForObject(
                    paymentStatusUrl,
                    request,
                    CreditCardPaymentResponse.class
            );
            return response != null && CARD_STATUS.equalsIgnoreCase(response.getStatus());
        } catch (Exception e) {
            // Log the error and assume failure or throw a custom exception
            log.error(String.format(Message.CARD_PAYMENT_EXCEPTION,e.getMessage()));
            return false;
        }
    }
}
