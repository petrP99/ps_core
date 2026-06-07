package com.pers.validation;

import com.pers.validation.impl.TransferInfoValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@Constraint(validatedBy = TransferInfoValidator.class)
public @interface TransferInfo {

    String message() default "All fields must be completed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
