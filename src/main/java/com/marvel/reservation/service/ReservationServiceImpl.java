package com.marvel.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvel.reservation.constant.Constants;
import com.marvel.reservation.dto.ReservationRequest;
import com.marvel.reservation.dto.ReservationResponse;
import com.marvel.reservation.exception.PaymentException;
import com.marvel.reservation.exception.ValidationException;
import com.marvel.reservation.model.Reservation;
import com.marvel.reservation.model.enums.PaymentMode;
import com.marvel.reservation.model.enums.ReservationStatus;
import com.marvel.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final CreditCardPaymentService creditCardPaymentService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ReservationResponse confirmReservation(ReservationRequest request) {
        Reservation reservation;
        try {
            // Convert DTO to entity
            reservation = objectMapper.convertValue(request, Reservation.class);
        } catch (IllegalArgumentException e) {
            log.error(Constants.FAILED_MAPPING, e);
            throw new RuntimeException(String.format(Constants.RUNTIME_EXCEPTION, e.getMessage()));
        }

        // Determine reservation status based on payment mode
        ReservationStatus status = switch (request.getPaymentMode()) {
            case CASH -> ReservationStatus.CONFIRMED;

            case CREDIT_CARD -> {
                if (request.getPaymentReference() == null || request.getPaymentReference().isBlank()) {
                    throw new ValidationException(Constants.PAYMENT_REFERENCE_REQUIRED_CC);
                }
                boolean isConfirmed = creditCardPaymentService.isPaymentConfirmed(request.getPaymentReference());
                if (isConfirmed) {
                    yield ReservationStatus.CONFIRMED;
                } else {
                    throw new PaymentException(Constants.CREDIT_CARD_NOT_CONFIRMED);
                }
            }

            case BANK_TRANSFER -> {
                if (request.getPaymentReference() == null || request.getPaymentReference().isBlank()) {
                    throw new ValidationException(Constants.PAYMENT_REFERENCE_REQUIRED_BANK);
                }
                yield ReservationStatus.PENDING_PAYMENT;
            }
        };

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
        String[] parts = transactionDescription.trim().split("\\s+");
        if (parts.length < 2) {
            log.error(String.format(Constants.INVALID_TRANSACTION_FORMAT, transactionDescription));
            throw new ValidationException(String.format(Constants.INVALID_TRANSACTION_FORMAT, transactionDescription));
        }

        String reservationIdString = parts[1];
        if (reservationIdString.length() != 8) {
            log.error(String.format(Constants.INVALID_RESERVATION_ID_LENGTH, reservationIdString));
            throw new ValidationException(String.format(Constants.INVALID_RESERVATION_ID_LENGTH, reservationIdString));
        }

        Optional<Reservation> optionalReservation = reservationRepository.findByPaymentReference(reservationIdString);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            if (reservation.getStatus() == ReservationStatus.PENDING_PAYMENT) {
                reservation.setStatus(ReservationStatus.CONFIRMED);
                reservationRepository.save(reservation);
                log.info(String.format(Constants.RESERVATION_CONFIRMED_BANK, reservation.getId()));
            } else {
                log.info(String.format(Constants.RESERVATION_ALREADY_CONFIRMED, reservation.getId(), reservation.getStatus()));
            }
        } else {
            log.warn(String.format(Constants.RESERVATION_NOT_FOUND, reservationIdString));
        }
    }

    @Override
    public List<Reservation> findPendingBankTransferReservations(LocalDate date) {
        // Cutoff date is 2 days before the given date
        LocalDate cutoffDate = date.minusDays(2);

        return reservationRepository.findByStatusAndCreatedAtBefore(
                        ReservationStatus.PENDING_PAYMENT,
                        cutoffDate
                )
                .stream()
                .filter(r -> r.getPaymentMode() == PaymentMode.BANK_TRANSFER)
                .toList();
    }

    @Override
    @Transactional
    public void cancelReservation(Long reservationId) {
        reservationRepository.findById(reservationId).ifPresent(reservation -> {
            if (reservation.getStatus() == ReservationStatus.PENDING_PAYMENT) {
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(reservation);
                log.info(String.format(Constants.RESERVATION_CANCELLED, reservationId));
            }
        });
    }
}
