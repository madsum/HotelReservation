package com.marvel.reservation.service;

import com.marvel.reservation.dto.ReservationRequest;
import com.marvel.reservation.dto.ReservationResponse;
import com.marvel.reservation.model.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    ReservationResponse confirmReservation(ReservationRequest request);
    void confirmBankTransferPayment(String transactionDescription);
    List<Reservation> findPendingBankTransferReservations(LocalDate date);
    void cancelReservation(Long reservationId);
}
