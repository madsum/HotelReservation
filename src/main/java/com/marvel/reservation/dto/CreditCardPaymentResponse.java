package com.marvel.reservation.dto;

import lombok.Data;
import lombok.Getter;

//@Data
@Getter
public class CreditCardPaymentResponse {
    private String status; // e.g., CONFIRMED, FAILED, PENDING
}
