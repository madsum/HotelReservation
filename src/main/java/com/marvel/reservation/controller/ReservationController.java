package com.marvel.reservation.controller;

import com.marvel.reservation.dto.ReservationRequest;
import com.marvel.reservation.dto.ReservationResponse;
import com.marvel.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/confirm")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse confirmReservation(@Valid @RequestBody ReservationRequest request) {
        return reservationService.confirmReservation(request);
    }
}
