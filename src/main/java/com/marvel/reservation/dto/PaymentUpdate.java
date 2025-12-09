package com.marvel.reservation.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentUpdate {
    private String paymentId;
    private String debtorAccountNumber;
    private BigDecimal amountReceived;
    private String transactionDescription;
}
