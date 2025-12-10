package com.marvel.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvel.reservation.dto.ReservationRequest;
import com.marvel.reservation.model.enums.PaymentMode;
import com.marvel.reservation.model.enums.ReservationStatus;
import com.marvel.reservation.model.enums.RoomSegment;
import com.marvel.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        reservationRepository.deleteAll();
    }

    private ReservationRequest createBaseRequest(PaymentMode mode, String ref) {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerName("Integration Test User");
        request.setRoomNumber("IT-101");
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setRoomSegment(RoomSegment.LARGE);
        request.setPaymentMode(mode);
        request.setPaymentReference(ref);
        return request;
    }


    @Test
    void confirmReservation_CASH_shouldReturnConfirmed() throws Exception {
        ReservationRequest request = createBaseRequest(PaymentMode.CASH, "CASH-REF-1");

        mockMvc.perform(post("/api/v1/reservations/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationStatus", is(ReservationStatus.CONFIRMED.name())));
    }

        @Test
    void confirmReservation_BANK_TRANSFER_shouldReturnPendingPayment() throws Exception {
        ReservationRequest request = createBaseRequest(PaymentMode.BANK_TRANSFER, "P4145478");

        mockMvc.perform(post("/api/v1/reservations/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationStatus", is(ReservationStatus.PENDING_PAYMENT.name())));
    }

    @Test
    void confirmReservation_BANK_TRANSFER_shouldFail_whenReferenceIsMissing() throws Exception {
        ReservationRequest request = createBaseRequest(PaymentMode.BANK_TRANSFER, null);

        mockMvc.perform(post("/api/v1/reservations/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message")
                        .value("Payment reference is required for Bank Transfer payment."));    }

    @Test
    void confirmReservation_CREDIT_CARD_shouldReturnConfirmed_whenPaymentIsSuccessful() throws Exception {
        ReservationRequest request = createBaseRequest(PaymentMode.CREDIT_CARD, "CC-SUCCESS-123");

        mockMvc.perform(post("/api/v1/reservations/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationStatus", is(ReservationStatus.CONFIRMED.name())));
    }

    @Test
    void confirmReservation_CREDIT_CARD_shouldFail_whenPaymentIsNotSuccessful() throws Exception {
        ReservationRequest request = createBaseRequest(PaymentMode.CREDIT_CARD, "CC-FAIL-123");

        mockMvc.perform(post("/api/v1/reservations/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // PaymentException is expected to result in 400 Bad Request

    }


    @Test
    void confirmReservation_shouldFail_whenReservationIsTooLong() throws Exception {
        ReservationRequest request = createBaseRequest(PaymentMode.CASH, null);
        request.setEndDate(LocalDate.now().plusDays(32)); // 31 days is too long

        mockMvc.perform(post("/api/v1/reservations/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
