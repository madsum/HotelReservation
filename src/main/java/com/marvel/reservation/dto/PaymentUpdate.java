package com.marvel.reservation.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

//@Data
@Getter
@Setter
public class PaymentUpdate {
    private String paymentId;
    private String debtorAccountNumber;
    private BigDecimal amountReceived;
    private String transactionDescription;
}
