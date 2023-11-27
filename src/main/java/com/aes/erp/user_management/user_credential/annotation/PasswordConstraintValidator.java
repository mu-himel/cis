package com.aes.erp.user_management.user_credential.annotation;

import com.aes.erp.config.Constants;
import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class documentation Comments to be added
 * */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                /** at least 8 characters */
                new LengthRule(Constants.PASSWORD_MIN_LENGTH, 255),

                /** at least one upper-case character */
                new CharacterRule(EnglishCharacterData.UpperCase, 1),

                 /** at least one lower-case character */
                new CharacterRule(EnglishCharacterData.LowerCase, 1),

                 /** at least one digit character */
                new CharacterRule(EnglishCharacterData.Digit, 1),

                 /** at least one symbol (special character) */
                new CharacterRule(EnglishCharacterData.Special, 1),

                 /** no whitespace */
                new WhitespaceRule()

        ));
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        List<String> messages = validator.getMessages(result);
        String messageTemplate = messages.stream()
                .collect(Collectors.joining(","));

        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;
    }
}
