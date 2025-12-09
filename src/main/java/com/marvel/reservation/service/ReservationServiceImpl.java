package com.marvel.reservation.service;

import com.marvel.reservation.dto.ReservationRequest;
import com.marvel.reservation.dto.ReservationResponse;
import com.marvel.reservation.exception.PaymentException;
import com.marvel.reservation.exception.ValidationException;
import com.marvel.reservation.model.Reservation;
import com.marvel.reservation.model.enums.PaymentMode;
import com.marvel.reservation.model.enums.ReservationStatus;
import com.marvel.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Log
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final CreditCardPaymentService creditCardPaymentService;

    @Override
    @Transactional
    public ReservationResponse confirmReservation(ReservationRequest request) {
        // Validation: A room cannot be reserved for more than 30 days and less than 1 day
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days > 30 || days < 1) {
            throw new ValidationException("Reservation period must be between 1 and 30 days.");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ValidationException("Start date cannot be after end date.");
        }

        Reservation reservation = new Reservation();
        reservation.setCustomerName(request.getCustomerName());
        reservation.setRoomNumber(request.getRoomNumber());
        reservation.setStartDate(request.getStartDate());
        reservation.setEndDate(request.getEndDate());
        reservation.setRoomSegment(request.getRoomSegment());
        reservation.setPaymentMode(request.getPaymentMode());
        reservation.setPaymentReference(request.getPaymentReference());

        ReservationStatus status;

        switch (request.getPaymentMode()) {
            case CASH:
                //If mode of payment is cash, room must be confirmed immediately
                status = ReservationStatus.CONFIRMED;
                break;
            case CREDIT_CARD:
                //If mode of payment is credit card, call rest api credit-card-payment-service
                // to retrieve the status of the payment. If credit payment is confirmed, then
                // confirm the room else throw an error
                if (request.getPaymentReference() == null || request.getPaymentReference().isBlank()) {
                    throw new ValidationException("Payment reference is required for Credit Card payment.");
                }
                boolean isConfirmed = creditCardPaymentService.isPaymentConfirmed(request.getPaymentReference());
                if (isConfirmed) {
                    status = ReservationStatus.CONFIRMED;
                } else {
                    throw new PaymentException("Credit card payment not confirmed. Reservation failed.");
                }
                break;
            case BANK_TRANSFER:
                // If mode of payment is bank transfer, room must be booked with pending payment status.
                if (request.getPaymentReference() == null || request.getPaymentReference().isBlank()) {
                    throw new ValidationException("Payment reference is required for Bank Transfer payment.");
                }
                status = ReservationStatus.PENDING_PAYMENT;
                break;
            default:
                throw new ValidationException("Invalid payment mode.");
        }

        reservation.setStatus(status);
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponse.builder()
                .reservationId(savedReservation.getId())
                .reservationStatus(savedReservation.getStatus())
                .build();
    }

    @Override
    @Transactional
    public void confirmBankTransferPayment(String transactionDescription) {
        // The transactionDescription format is: <E2E unique id(10 character)> <reservationId (8 characters)>
        // We need to extract the reservationId (8 characters)
        String[] parts = transactionDescription.trim().split("\\s+");
        if (parts.length < 2) {
            log.severe("Invalid transaction description format: " + transactionDescription);
            throw new ValidationException("Invalid transaction description format: " + transactionDescription);
        }

        // Assuming the reservationId is the second part and is 8 characters long
        String reservationIdString = parts[1];
        if (reservationIdString.length() != 8) {
            log.severe("Reservation ID in transaction description is not 8 characters: " + reservationIdString);
            throw new ValidationException("Reservation ID in transaction description is not 8 characters: " + reservationIdString);
        }

        // Find the reservation by payment reference
        // Let's assume the paymentReference in the Reservation model stores this 8-character ID.
        // I will assume and search the 8-character string is the paymentReference
        Optional<Reservation> optionalReservation = reservationRepository.findByPaymentReference(reservationIdString);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            if (reservation.getStatus() == ReservationStatus.PENDING_PAYMENT) {
                reservation.setStatus(ReservationStatus.CONFIRMED);
                reservationRepository.save(reservation);
                log.info("Reservation " + reservation.getId() + " confirmed via bank transfer update.");
            } else {
                log.info("Reservation " + reservation.getId() + " is already " + reservation.getStatus() + ". No change made.");
            }
        } else {
            log.warning("Reservation not found for payment reference: " + reservationIdString);
        }
    }

    @Override
    public List<Reservation> findPendingBankTransferReservations(LocalDate date) {
        // Find all PENDING_PAYMENT reservations that were made 2 days before the given date
        LocalDate cutoffDate = date.minusDays(2);
        return reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING_PAYMENT)
                .filter(r -> r.getPaymentMode() == PaymentMode.BANK_TRANSFER)
                .filter(r -> r.getReservationDate().isBefore(cutoffDate) || r.getReservationDate().isEqual(cutoffDate))
                .toList();
    }

    @Override
    @Transactional
    public void cancelReservation(Long reservationId) {
        reservationRepository.findById(reservationId).ifPresent(reservation -> {
            if (reservation.getStatus() == ReservationStatus.PENDING_PAYMENT) {
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(reservation);
                log.info("Reservation " + reservationId + " automatically cancelled due to overdue payment.");
            }
        });
    }
}
