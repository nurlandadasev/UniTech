package az.unibank.authserver.util;

import az.unibank.authserver.models.User;
import az.unibank.authserver.repo.UserRepository;
import az.unibank.commons.config.Constants;
import az.unibank.commons.enums.PasswordValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

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