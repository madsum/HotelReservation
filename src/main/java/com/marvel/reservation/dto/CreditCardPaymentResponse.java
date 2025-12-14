package com.marvel.reservation.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CreditCardPaymentResponse {
    private String status; // e.g., CONFIRMED, FAILED, PENDING
}
