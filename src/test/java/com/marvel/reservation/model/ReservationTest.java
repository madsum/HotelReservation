package com.marvel.reservation.model;

import com.marvel.reservation.model.enums.PaymentMode;
import com.marvel.reservation.model.enums.ReservationStatus;
import com.marvel.reservation.model.enums.RoomSegment;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationTest {

    @Test
    void testDefaultConstructorAndSetters() {
        Reservation r = new Reservation();

        r.setId(1L);
        r.setCustomerName("John Doe");
        r.setRoomNumber("101A");
        r.setStartDate(LocalDate.of(2025, 1, 1));
        r.setEndDate(LocalDate.of(2025, 1, 10));
        r.setRoomSegment(RoomSegment.MEDIUM);
        r.setPaymentMode(PaymentMode.CREDIT_CARD);
        r.setPaymentReference("ABC123");
        r.setStatus(ReservationStatus.CONFIRMED);

        assertThat(r.getId()).isEqualTo(1L);
        assertThat(r.getCustomerName()).isEqualTo("John Doe");
        assertThat(r.getRoomNumber()).isEqualTo("101A");
        assertThat(r.getStartDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(r.getEndDate()).isEqualTo(LocalDate.of(2025, 1, 10));
        assertThat(r.getRoomSegment()).isEqualTo(RoomSegment.MEDIUM);
        assertThat(r.getPaymentMode()).isEqualTo(PaymentMode.CREDIT_CARD);
        assertThat(r.getPaymentReference()).isEqualTo("ABC123");
        assertThat(r.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }



    @Test
    void testDefaultReservationDateIsSet() {
        Reservation r = new Reservation();

        assertThat(r.getReservationDate())
                .isNotNull()
                .isEqualTo(LocalDate.now());
    }




    @Test
    void testEqualsAndHashCode() {
        Reservation r1 = new Reservation();
        Reservation r2 = new Reservation();

        r1.setId(1L);
        r1.setCustomerName("Alice");
        r1.setRoomNumber("50B");
        r1.setStartDate(LocalDate.of(2025, 5, 20));
        r1.setEndDate(LocalDate.of(2025, 5, 25));
        r1.setRoomSegment(RoomSegment.MEDIUM);
        r1.setPaymentMode(PaymentMode.CASH);
        r1.setPaymentReference("REF1");
        r1.setStatus(ReservationStatus.PENDING_PAYMENT);

        r2.setId(1L);
        r2.setCustomerName("Alice");
        r2.setRoomNumber("50B");
        r2.setStartDate(LocalDate.of(2025, 5, 20));
        r2.setEndDate(LocalDate.of(2025, 5, 25));
        r2.setRoomSegment(RoomSegment.MEDIUM);
        r2.setPaymentMode(PaymentMode.CASH);
        r2.setPaymentReference("REF1");
        r2.setStatus(ReservationStatus.PENDING_PAYMENT);

        assertThat(r1)
                .isEqualTo(r2)
                .hasSameHashCodeAs(r2);
    }

    @Test
    void testToString() {
        Reservation r = new Reservation();
        r.setCustomerName("Bob");
        r.setRoomNumber("22C");

        assertThat(r.toString())
                .contains("customerName=Bob")
                .contains("roomNumber=22C");
    }
}
