package az.unibank.authserver.util;

import az.unibank.commons.config.Constants;
import az.unibank.commons.enums.PasswordValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class PasswordUtils {

    public PasswordValidationResult validatePassword(String name, String pin, String password) {

        if (!password.matches(Constants.PASSWORD_VALIDATION_REGEX)) {
            return PasswordValidationResult.NOT_COMPLEX;
        }

        for (String exp : name.split(" ")) {
            if (exp.trim().length() > 1 && password.toLowerCase().contains(exp.trim().toLowerCase())) {
                return PasswordValidationResult.CONTAINS_USER_INFO;
            }
        }

        if (password.toLowerCase().contains(pin)) {
            return PasswordValidationResult.CONTAINS_USER_INFO;
        }

        return PasswordValidationResult.VALID;
    }


}