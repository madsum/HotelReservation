package com.marvel.reservation.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvel.reservation.dto.PaymentUpdate;
import com.marvel.reservation.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentUpdateListenerTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentUpdateListener listener;

    private PaymentUpdate paymentUpdate;
    private String jsonMessage;
    private String transactionDescription;

    @BeforeEach
    void setUp() {
        transactionDescription = "1401541457 P4145478";
        paymentUpdate = new PaymentUpdate();
        paymentUpdate.setPaymentId("12345");
        paymentUpdate.setDebtorAccountNumber("ACC-001");
        paymentUpdate.setAmountReceived(new BigDecimal("100.00"));
        paymentUpdate.setTransactionDescription(transactionDescription);

        try {
            jsonMessage = new ObjectMapper().writeValueAsString(paymentUpdate);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void handlePaymentUpdate_shouldCallConfirmBankTransferPayment_onSuccess() throws JsonProcessingException {
        // Arrange
        when(objectMapper.readValue(jsonMessage, PaymentUpdate.class)).thenReturn(paymentUpdate);

        // Act
        listener.handlePaymentUpdate(jsonMessage);

        // Assert
        verify(reservationService, times(1)).confirmBankTransferPayment(transactionDescription);
    }

    @Test
    void handlePaymentUpdate_shouldHandleJsonProcessingException_onFailure() throws JsonProcessingException {
        // Arrange
        String invalidJson = "invalid json";
        when(objectMapper.readValue(invalidJson, PaymentUpdate.class)).thenThrow(JsonProcessingException.class);

        // Act
        listener.handlePaymentUpdate(invalidJson);

        // Assert
        verify(reservationService, never()).confirmBankTransferPayment(anyString());
        // In a real test, we would verify logging of the error, but here we verify no further processing occurs.
    }
}
