package az.unibank.authserver.dto.request;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterNewUser {

    private String name;
    private String phone;
    private String pin;
    private String password;


}
