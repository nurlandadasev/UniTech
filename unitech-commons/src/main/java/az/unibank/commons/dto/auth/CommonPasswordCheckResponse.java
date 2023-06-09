package az.unibank.commons.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonPasswordCheckResponse {

    private int code;
    private boolean exists;
}