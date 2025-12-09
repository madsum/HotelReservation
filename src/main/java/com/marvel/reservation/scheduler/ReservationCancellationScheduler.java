package com.marvel.reservation.scheduler;

import com.marvel.reservation.model.Reservation;
import com.marvel.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Log
@RequiredArgsConstructor
public class ReservationCancellationScheduler {
    private final ReservationService reservationService;
    // Run every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void cancelOverdueBankTransferReservations() {
        log.info("Running scheduled task to cancel overdue bank transfer reservations...");
        // Automatically cancel the reservation, If the payment is done using a bank transfer
        // and total amount is not received 2 days after the reservation date.
        LocalDate today = LocalDate.now();
        List<Reservation> overdueReservations = reservationService.findPendingBankTransferReservations(today);

        for (Reservation reservation : overdueReservations) {
            reservationService.cancelReservation(reservation.getId());
        }
        log.info("Finished scheduled task. " + overdueReservations.size() + " reservations checked.");
    }
}
