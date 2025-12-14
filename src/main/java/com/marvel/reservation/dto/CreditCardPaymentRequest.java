package com.marvel.reservation.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CreditCardPaymentRequest {
    private String paymentReference;
}
