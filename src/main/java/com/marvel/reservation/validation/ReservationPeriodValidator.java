package com.marvel.reservation.validation;

import com.marvel.reservation.dto.ReservationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.temporal.ChronoUnit;

public class ReservationPeriodValidator
        implements ConstraintValidator<ValidReservationPeriod, ReservationRequest> {

    @Override
    public boolean isValid(ReservationRequest request, ConstraintValidatorContext context) {

        if (request.getStartDate() == null || request.getEndDate() == null) {
            return true;
        }

        // Start date must be before end date
        if (request.getStartDate().isAfter(request.getEndDate())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Start date cannot be after end date"
            ).addPropertyNode("startDate").addConstraintViolation();
            return false;
        }

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        if (days < 1 || days > 30) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Reservation period must be between 1 and 30 days"
            ).addPropertyNode("endDate").addConstraintViolation();
            return false;
        }

        return true;
    }
}
