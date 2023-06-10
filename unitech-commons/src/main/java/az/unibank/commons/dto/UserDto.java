package az.unibank.commons.dto;

import az.unibank.commons.dto.auth.RoleDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {


    private long id;
    private String name;
    private String pin;
    private String password;
    private String phone;
    private RoleDTO role;
    private LocalDateTime createdDate;
    private LocalDateTime lastLoginDate;
    private int isBlocked;
    private LocalDateTime blockedDate;
    private List<AccountDto> accountList = new ArrayList<>();

}
