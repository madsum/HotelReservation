package com.marvel.reservation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReservationPeriodValidator.class)
@Documented
public @interface ValidReservationPeriod {

    String message() default "Invalid reservation period";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

