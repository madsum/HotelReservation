package com.marvel.reservation.dto;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

//@Data
@Getter
public class PaymentUpdate {
    private String paymentId;
    private String debtorAccountNumber;
    private BigDecimal amountReceived;
    private String transactionDescription;
}
