package com.marvel.reservation.dto;

import com.marvel.reservation.model.enums.PaymentMode;
import com.marvel.reservation.model.enums.RoomSegment;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
public class ReservationRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @NotNull(message = "Room segment is required")
    private RoomSegment roomSegment;

    @NotNull(message = "Mode of payment is required")
    private PaymentMode paymentMode;

    private String paymentReference;
}
