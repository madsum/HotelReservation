package com.marvel.reservation.constant;

public class Constants {

    // Reservation confirmation
    public static final String FAILED_MAPPING = "Failed to map ReservationRequest to Reservation entity";
    public static final String PAYMENT_REFERENCE_REQUIRED_CC = "Payment reference is required for Credit Card payment.";
    public static final String PAYMENT_REFERENCE_REQUIRED_BANK = "Payment reference is required for Bank Transfer payment.";
    public static final String CREDIT_CARD_NOT_CONFIRMED = "Credit card payment not confirmed. Reservation failed.";
    public static final String INVALID_PAYMENT_MODE = "Invalid payment mode.";

    // Bank transfer
    public static final String INVALID_TRANSACTION_FORMAT = "Invalid transaction description format: %s";
    public static final String INVALID_RESERVATION_ID_LENGTH = "Reservation ID in transaction description is not 8 characters: %s";
    public static final String RESERVATION_CONFIRMED_BANK = "Reservation %d confirmed via bank transfer update.";
    public static final String RESERVATION_ALREADY_CONFIRMED = "Reservation %d is already %s. No change made.";
    public static final String RESERVATION_NOT_FOUND = "Reservation not found for payment reference: %s";

    // Scheduler
    public static final String FINISHED_SCHEDULER = "Finished scheduled task. %d reservations checked.";
    public static final String NO_OVERDUE_RESERVATION = "No overdue reservation found";
    public static final String RUNNING_SCHEDULER_TASK = "Running scheduled task to cancel overdue bank transfer reservations...";

    // Kafka / external
    public static final String KAFKA_EXCEPTION = "Error processing Kafka message: %s";
    public static final String CARD_PAYMENT_EXCEPTION = "Error calling credit card payment service: %s";

    // Reservation cancellation
    public static final String RESERVATION_CANCELLED = "Reservation %d automatically cancelled due to overdue payment.";

    // KAFKA
    public static final String TOPIC_BANK_TRANSFER_PAYMENT_UPDATE = "bank-transfer-payment-update";
    public static final String GROUP_ROOM_RESERVATION = "room-reservation-group";

    public static final String RUNTIME_EXCEPTION =  "Internal server error while processing reservation %s";
}

