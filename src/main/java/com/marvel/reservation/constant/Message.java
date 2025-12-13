package com.marvel.reservation.constant;

public class Message {
    public static final String FINISHED_SCHEDULER = "Finished scheduled task. %d reservations checked.";
    public static final String NO_OVERDUE_RESERVATION = "No overdue reservation found";
    public static final String RUNNING_SCHEDULER_TASK = "Running scheduled task to cancel overdue bank transfer reservations...";
    public static final String KAFKA_EXCEPTION = "Error processing Kafka message: %s";
    public static final String CARD_PAYMENT_EXCEPTION = "Error calling credit card payment service: %s";
}
