package az.unibank.commons.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    private long id;
    private String accountNumber;
    private LocalDate endDate;

}
