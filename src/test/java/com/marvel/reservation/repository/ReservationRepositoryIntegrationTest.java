package com.marvel.reservation.repository;


import com.marvel.reservation.model.Reservation;
import com.marvel.reservation.model.enums.PaymentMode;
import com.marvel.reservation.model.enums.ReservationStatus;
import com.marvel.reservation.model.enums.RoomSegment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ReservationRepositoryIntegrationTest {

    @Autowired
    private ReservationRepository reservationRepository;

    private Reservation createReservation(String ref, ReservationStatus status, LocalDate reservationDate) {
        Reservation reservation = new Reservation();
        reservation.setCustomerName("Repo Test");
        reservation.setRoomNumber("R101");
        reservation.setStartDate(LocalDate.now().plusDays(1));
        reservation.setEndDate(LocalDate.now().plusDays(2));
        reservation.setRoomSegment(RoomSegment.SMALL);
        reservation.setPaymentMode(PaymentMode.BANK_TRANSFER);
        reservation.setPaymentReference(ref);
        reservation.setStatus(status);
        reservation.setReservationDate(reservationDate);
        return reservationRepository.save(reservation);
    }

    @Test
    void findByPaymentReference_shouldReturnReservation_whenFound() {
        // Arrange
        String paymentRef = "UNIQUE-REF-123";
        LocalDate cutoff = LocalDate.now();
        Reservation savedReservation = createReservation(paymentRef, ReservationStatus.PENDING_PAYMENT, cutoff.minusDays(2));

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

    @Test
    void findByStatusAndCreatedAtBefore_shouldReturnReservationsBeforeCutoff() {
        // Arrange
        LocalDate cutoff = LocalDate.now();
        Reservation overdue = createReservation("REF-OVERDUE", ReservationStatus.PENDING_PAYMENT, cutoff.minusDays(2));
        createReservation("REF-NOT-OVERDUE", ReservationStatus.PENDING_PAYMENT, cutoff.plusDays(1)); // after cutoff
        createReservation("REF-CONFIRMED", ReservationStatus.CONFIRMED, cutoff.minusDays(2)); // wrong status

        // Act
        List<Reservation> result = reservationRepository.findByStatusAndCreatedAtBefore(
                ReservationStatus.PENDING_PAYMENT, cutoff);

        // Assert
        assertThat(result)
                .hasSize(1)
                .extracting(Reservation::getPaymentReference)
                .containsExactly("REF-OVERDUE");
    }

    @Test
    void findByStatusAndCreatedAtBefore_shouldReturnEmpty_whenNoMatch() {
        // Arrange
        LocalDate cutoff = LocalDate.now();
        createReservation("REF-FUTURE", ReservationStatus.PENDING_PAYMENT, cutoff.plusDays(5));
        createReservation("REF-CONFIRMED", ReservationStatus.CONFIRMED, cutoff.minusDays(5));

        // Act
        List<Reservation> result = reservationRepository.findByStatusAndCreatedAtBefore(
                ReservationStatus.PENDING_PAYMENT, cutoff.minusDays(1));

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByStatusAndCreatedAtBefore_shouldReturnMultipleMatchingReservations() {
        // Arrange
        LocalDate cutoff = LocalDate.now();
        Reservation r1 = createReservation("REF-1", ReservationStatus.PENDING_PAYMENT, cutoff.minusDays(3));
        Reservation r2 = createReservation("REF-2", ReservationStatus.PENDING_PAYMENT, cutoff.minusDays(2));

        // Act
        List<Reservation> result = reservationRepository.findByStatusAndCreatedAtBefore(
                ReservationStatus.PENDING_PAYMENT, cutoff);

        // Assert
        assertThat(result)
                .hasSize(2)
                .extracting(Reservation::getPaymentReference)
                .containsExactlyInAnyOrder("REF-1", "REF-2");
    }




}
