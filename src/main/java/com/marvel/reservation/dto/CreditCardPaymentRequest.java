package com.marvel.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

///@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreditCardPaymentRequest {
    private String paymentReference;
}
