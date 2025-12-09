package com.marvel.reservation.repository;


import com.marvel.reservation.model.Reservation;
import com.marvel.reservation.model.enums.PaymentMode;
import com.marvel.reservation.model.enums.ReservationStatus;
import com.marvel.reservation.model.enums.RoomSegment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ReservationRepositoryIntegrationTest {

    @Autowired
    private ReservationRepository reservationRepository;

    private Reservation createReservation(String ref, ReservationStatus status) {
        Reservation reservation = new Reservation();
        reservation.setCustomerName("Repo Test");
        reservation.setRoomNumber("R101");
        reservation.setStartDate(LocalDate.now().plusDays(1));
        reservation.setEndDate(LocalDate.now().plusDays(2));
        reservation.setRoomSegment(RoomSegment.SMALL);
        reservation.setPaymentMode(PaymentMode.BANK_TRANSFER);
        reservation.setPaymentReference(ref);
        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }

    @Test
    void findByPaymentReference_shouldReturnReservation_whenFound() {
        // Arrange
        String paymentRef = "UNIQUE-REF-123";
        Reservation savedReservation = createReservation(paymentRef, ReservationStatus.PENDING_PAYMENT);

        // Act
        Optional<Reservation> foundReservation = reservationRepository.findByPaymentReference(paymentRef);

        // Assert
        assertThat(foundReservation).isPresent();
        assertThat(foundReservation.get().getId()).isEqualTo(savedReservation.getId());
        assertThat(foundReservation.get().getPaymentReference()).isEqualTo(paymentRef);
    }

    @Test
    void findByPaymentReference_shouldReturnEmpty_whenNotFound() {
        // Act
        Optional<Reservation> foundReservation = reservationRepository.findByPaymentReference("NON-EXISTENT");

        // Assert
        assertThat(foundReservation).isEmpty();
    }
}
