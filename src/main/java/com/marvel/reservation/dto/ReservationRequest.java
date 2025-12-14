package com.marvel.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.marvel.reservation.model.enums.PaymentMode;
import com.marvel.reservation.model.enums.RoomSegment;
import com.marvel.reservation.validation.ValidReservationPeriod;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@ValidReservationPeriod  // <-- apply the custom class-level validator
public class ReservationRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message = "Room segment is required")
    private RoomSegment roomSegment;

    @NotNull(message = "Mode of payment is required")
    private PaymentMode paymentMode;

    private String paymentReference;
}
