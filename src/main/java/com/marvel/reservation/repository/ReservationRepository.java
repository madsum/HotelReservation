package com.marvel.reservation.repository;

import com.marvel.reservation.model.Reservation;
import com.marvel.reservation.model.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByPaymentReference(String paymentReference);

    // Find all reservations with the given status whose createdAt timestamp is before the given cutoff.
    @Query("SELECT r FROM Reservation r WHERE r.status = :status AND r.reservationDate   < :cutoff")
            List<Reservation> findByStatusAndCreatedAtBefore(
            @Param("status") ReservationStatus status,
            @Param("cutoff") LocalDate cutoff
    );
}

