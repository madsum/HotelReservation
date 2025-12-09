package com.marvel.reservation.dto;

import lombok.Data;

@Data
public class CreditCardPaymentResponse {
    private String status; // e.g., CONFIRMED, FAILED, PENDING
}
