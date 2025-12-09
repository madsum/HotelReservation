package com.marvel.reservation.service;

import com.marvel.reservation.dto.ReservationRequest;
import com.marvel.reservation.dto.ReservationResponse;
import com.marvel.reservation.exception.PaymentException;
import com.marvel.reservation.exception.ValidationException;
import com.marvel.reservation.model.Reservation;
import com.marvel.reservation.model.enums.PaymentMode;
import com.marvel.reservation.model.enums.ReservationStatus;
import com.marvel.reservation.model.enums.RoomSegment;
import com.marvel.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CreditCardPaymentService creditCardPaymentService;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private ReservationRequest baseRequest;
    private Reservation savedReservation;

    @BeforeEach
    void setUp() {
        baseRequest = new ReservationRequest();
        baseRequest.setCustomerName("Test Customer");
        baseRequest.setRoomNumber("101");
        baseRequest.setStartDate(LocalDate.now().plusDays(1));
        baseRequest.setEndDate(LocalDate.now().plusDays(3));
        baseRequest.setRoomSegment(RoomSegment.MEDIUM);

        savedReservation = new Reservation();
        savedReservation.setId(1L);
        savedReservation.setCustomerName(baseRequest.getCustomerName());
        savedReservation.setRoomNumber(baseRequest.getRoomNumber());
        savedReservation.setStartDate(baseRequest.getStartDate());
        savedReservation.setEndDate(baseRequest.getEndDate());
        savedReservation.setRoomSegment(baseRequest.getRoomSegment());
    }

    @Test
    void confirmReservation_shouldThrowException_whenReservationExceeds30Days() {
        baseRequest.setStartDate(LocalDate.now().plusDays(1));
        baseRequest.setEndDate(LocalDate.now().plusDays(32)); // 31 days
        baseRequest.setPaymentMode(PaymentMode.CASH);

        assertThrows(ValidationException.class, () -> reservationService.confirmReservation(baseRequest));
    }

    @Test
    void confirmReservation_shouldThrowException_whenStartDateIsAfterEndDate() {
        baseRequest.setStartDate(LocalDate.now().plusDays(5));
        baseRequest.setEndDate(LocalDate.now().plusDays(3));
        baseRequest.setPaymentMode(PaymentMode.CASH);

        assertThrows(ValidationException.class, () -> reservationService.confirmReservation(baseRequest));
    }

    @Test
    void confirmReservation_CASH_shouldConfirmImmediately() {
        baseRequest.setPaymentMode(PaymentMode.CASH);
        savedReservation.setStatus(ReservationStatus.CONFIRMED);
        savedReservation.setPaymentMode(PaymentMode.CASH);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        ReservationResponse response = reservationService.confirmReservation(baseRequest);

        assertEquals(ReservationStatus.CONFIRMED, response.getReservationStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(creditCardPaymentService, never()).isPaymentConfirmed(anyString());
    }

    @Test
    void confirmReservation_CREDIT_CARD_shouldConfirm_whenPaymentIsConfirmed() {
        baseRequest.setPaymentMode(PaymentMode.CREDIT_CARD);
        baseRequest.setPaymentReference("CC-REF-123");
        savedReservation.setStatus(ReservationStatus.CONFIRMED);
        savedReservation.setPaymentMode(PaymentMode.CREDIT_CARD);
        savedReservation.setPaymentReference("CC-REF-123");

        when(creditCardPaymentService.isPaymentConfirmed("CC-REF-123")).thenReturn(true);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        ReservationResponse response = reservationService.confirmReservation(baseRequest);

        assertEquals(ReservationStatus.CONFIRMED, response.getReservationStatus());
        verify(creditCardPaymentService, times(1)).isPaymentConfirmed("CC-REF-123");
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void confirmReservation_CREDIT_CARD_shouldThrowException_whenPaymentIsNotConfirmed() {
        baseRequest.setPaymentMode(PaymentMode.CREDIT_CARD);
        baseRequest.setPaymentReference("CC-REF-123");

        when(creditCardPaymentService.isPaymentConfirmed("CC-REF-123")).thenReturn(false);

        assertThrows(PaymentException.class, () -> reservationService.confirmReservation(baseRequest));

        verify(creditCardPaymentService, times(1)).isPaymentConfirmed("CC-REF-123");
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void confirmReservation_CREDIT_CARD_shouldThrowException_whenReferenceIsMissing() {
        baseRequest.setPaymentMode(PaymentMode.CREDIT_CARD);
        baseRequest.setPaymentReference(null);

        assertThrows(ValidationException.class, () -> reservationService.confirmReservation(baseRequest));

        verify(creditCardPaymentService, never()).isPaymentConfirmed(anyString());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }


    @Test
    void confirmReservation_BANK_TRANSFER_shouldSetPendingPayment() {
        baseRequest.setPaymentMode(PaymentMode.BANK_TRANSFER);
        baseRequest.setPaymentReference("P4145478");
        savedReservation.setStatus(ReservationStatus.PENDING_PAYMENT);
        savedReservation.setPaymentMode(PaymentMode.BANK_TRANSFER);
        savedReservation.setPaymentReference("P4145478");

        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        ReservationResponse response = reservationService.confirmReservation(baseRequest);

        assertEquals(ReservationStatus.PENDING_PAYMENT, response.getReservationStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(creditCardPaymentService, never()).isPaymentConfirmed(anyString());
    }

    @Test
    void confirmReservation_BANK_TRANSFER_shouldThrowException_whenReferenceIsMissing() {
        baseRequest.setPaymentMode(PaymentMode.BANK_TRANSFER);
        baseRequest.setPaymentReference(null);

        assertThrows(ValidationException.class, () -> reservationService.confirmReservation(baseRequest));

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void confirmBankTransferPayment_shouldConfirmReservation_whenPending() {
        String paymentRef = "P4145478";
        String transactionDesc = "1401541457 " + paymentRef;
        Reservation pendingReservation = new Reservation();
        pendingReservation.setId(1L);
        pendingReservation.setStatus(ReservationStatus.PENDING_PAYMENT);

        when(reservationRepository.findByPaymentReference(paymentRef)).thenReturn(Optional.of(pendingReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(pendingReservation);

        reservationService.confirmBankTransferPayment(transactionDesc);

        assertEquals(ReservationStatus.CONFIRMED, pendingReservation.getStatus());
        verify(reservationRepository, times(1)).save(pendingReservation);
    }

    @Test
    void confirmBankTransferPayment_shouldDoNothing_whenAlreadyConfirmed() {
        String paymentRef = "P4145478";
        String transactionDesc = "1401541457 " + paymentRef;
        Reservation confirmedReservation = new Reservation();
        confirmedReservation.setId(1L);
        confirmedReservation.setStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findByPaymentReference(paymentRef)).thenReturn(Optional.of(confirmedReservation));

        reservationService.confirmBankTransferPayment(transactionDesc);

        verify(reservationRepository, never()).save(confirmedReservation);
    }

    @Test
    void confirmBankTransferPayment_shouldDoNothing_whenReservationNotFound() {
        String paymentRef = "P4145478";
        String transactionDesc = "1401541457 " + paymentRef;

        when(reservationRepository.findByPaymentReference(paymentRef)).thenReturn(Optional.empty());

        reservationService.confirmBankTransferPayment(transactionDesc);

        verify(reservationRepository, never()).save(any(Reservation.class));
    }


    @Test
    void findPendingBankTransferReservations_shouldReturnOverdueReservations() {
        LocalDate today = LocalDate.now();
        LocalDate overdueDate = today.minusDays(2);
        LocalDate notOverdueDate = today.minusDays(1);

        Reservation overdueReservation = new Reservation();
        overdueReservation.setId(1L);
        overdueReservation.setStatus(ReservationStatus.PENDING_PAYMENT);
        overdueReservation.setPaymentMode(PaymentMode.BANK_TRANSFER);
        overdueReservation.setReservationDate(overdueDate);

        Reservation notOverdueReservation = new Reservation();
        notOverdueReservation.setId(2L);
        notOverdueReservation.setStatus(ReservationStatus.PENDING_PAYMENT);
        notOverdueReservation.setPaymentMode(PaymentMode.BANK_TRANSFER);
        notOverdueReservation.setReservationDate(notOverdueDate);

        Reservation confirmedReservation = new Reservation();
        confirmedReservation.setId(3L);
        confirmedReservation.setStatus(ReservationStatus.CONFIRMED);
        confirmedReservation.setPaymentMode(PaymentMode.BANK_TRANSFER);
        confirmedReservation.setReservationDate(overdueDate);

        when(reservationRepository.findAll()).thenReturn(List.of(overdueReservation, notOverdueReservation, confirmedReservation));

        List<Reservation> result = reservationService.findPendingBankTransferReservations(today);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    // --- cancelReservation Tests ---

    @Test
    void cancelReservation_shouldCancel_whenPending() {
        Reservation pendingReservation = new Reservation();
        pendingReservation.setId(1L);
        pendingReservation.setStatus(ReservationStatus.PENDING_PAYMENT);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(pendingReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(pendingReservation);

        reservationService.cancelReservation(1L);

        assertEquals(ReservationStatus.CANCELLED, pendingReservation.getStatus());
        verify(reservationRepository, times(1)).save(pendingReservation);
    }

    @Test
    void cancelReservation_shouldDoNothing_whenAlreadyConfirmed() {
        Reservation confirmedReservation = new Reservation();
        confirmedReservation.setId(1L);
        confirmedReservation.setStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(confirmedReservation));

        reservationService.cancelReservation(1L);

        verify(reservationRepository, never()).save(confirmedReservation);
    }
}
