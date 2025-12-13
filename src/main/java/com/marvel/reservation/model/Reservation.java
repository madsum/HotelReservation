package com.marvel.reservation.model;

import com.marvel.reservation.model.enums.PaymentMode;
import com.marvel.reservation.model.enums.ReservationStatus;
import com.marvel.reservation.model.enums.RoomSegment;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Reservation {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String customerName;

        private String roomNumber;

        private LocalDate startDate;

        private LocalDate endDate;

        @Enumerated(EnumType.STRING)
        private RoomSegment roomSegment;

        @Enumerated(EnumType.STRING)
        private PaymentMode paymentMode;

        private String paymentReference;

        @Enumerated(EnumType.STRING)
        private ReservationStatus status;

        private LocalDate reservationDate = LocalDate.now();
    }

