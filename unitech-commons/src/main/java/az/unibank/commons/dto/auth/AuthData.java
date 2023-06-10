package az.unibank.commons.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Builder
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthData {

    private long id;
    private String name;
    private String pin;
    private RoleDTO selectedRole;

}
