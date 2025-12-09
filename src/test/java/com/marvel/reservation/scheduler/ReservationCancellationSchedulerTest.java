package com.marvel.reservation.scheduler;

import com.marvel.reservation.model.Reservation;
import com.marvel.reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationCancellationSchedulerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationCancellationScheduler scheduler;

    @Test
    void cancelOverdueBankTransferReservations_shouldCancelAllOverdueReservations() {
        // Arrange
        Reservation r1 = new Reservation();
        r1.setId(1L);
        Reservation r2 = new Reservation();
        r2.setId(2L);

        List<Reservation> overdueReservations = List.of(r1, r2);

        when(reservationService.findPendingBankTransferReservations(any(LocalDate.class)))
                .thenReturn(overdueReservations);

        // Act
        scheduler.cancelOverdueBankTransferReservations();

        // Assert
        verify(reservationService, times(1)).findPendingBankTransferReservations(any(LocalDate.class));
        verify(reservationService, times(1)).cancelReservation(1L);
        verify(reservationService, times(1)).cancelReservation(2L);
    }

    @Test
    void cancelOverdueBankTransferReservations_shouldDoNothing_whenNoReservationsAreOverdue() {
        // Arrange
        when(reservationService.findPendingBankTransferReservations(any(LocalDate.class)))
                .thenReturn(List.of());

        // Act
        scheduler.cancelOverdueBankTransferReservations();

        // Assert
        verify(reservationService, times(1)).findPendingBankTransferReservations(any(LocalDate.class));
        verify(reservationService, never()).cancelReservation(anyLong());
    }
}
