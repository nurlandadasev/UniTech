package az.unibank.commons.dto;

import lombok.*;

import java.math.BigDecimal;
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
    private BigDecimal balance;
    private CurrencyDto currency;
}
