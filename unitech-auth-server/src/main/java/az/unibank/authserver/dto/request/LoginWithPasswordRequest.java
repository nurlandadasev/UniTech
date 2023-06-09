package az.unibank.authserver.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class LoginWithPasswordRequest {

    @NotNull
    private String pin;

    @NotNull
    private String password;

}