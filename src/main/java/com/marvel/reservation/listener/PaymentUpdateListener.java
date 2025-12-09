package com.marvel.reservation.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvel.reservation.dto.PaymentUpdate;
import com.marvel.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log
@RequiredArgsConstructor
public class PaymentUpdateListener {

    private final ReservationService reservationService;
    private final ObjectMapper objectMapper;

    // Assuming the Kafka message is a JSON string of the PaymentUpdate DTO
    @KafkaListener(topics = "bank-transfer-payment-update", groupId = "room-reservation-group")
    public void handlePaymentUpdate(String message) {
        try {
            PaymentUpdate paymentUpdate = objectMapper.readValue(message, PaymentUpdate.class);
            // The logic for confirming the booking is based on the transactionDescription
            // which contains the reservationId.
            reservationService.confirmBankTransferPayment(paymentUpdate.getTransactionDescription());
        } catch (JsonProcessingException e) {
            log.severe("Error processing Kafka message: " + e.getMessage());
            // In a real application, we would handle this with a Dead Letter Queue (DLQ)
        }
    }
}
