package com.marvel.reservation.dto;

import com.marvel.reservation.model.enums.ReservationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationResponse {
    private Long reservationId;
    private ReservationStatus reservationStatus;
}
