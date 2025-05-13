package com.samyookgoo.palgoosam.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DivisibleByValidator implements ConstraintValidator<DivisibleBy, Integer> {

    private int unit;

    @Override
    public void initialize(DivisibleBy constraintAnnotation) {
        this.unit = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value == null || value % unit == 0;
    }
}