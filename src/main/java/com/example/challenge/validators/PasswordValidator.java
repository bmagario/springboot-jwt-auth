package com.example.challenge.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*\\d.*\\d)(?=.*[a-z]).{8,12}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return pattern.matcher(value).matches();
    }
}
