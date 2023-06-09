package az.unibank.commons.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferMoneyRequestDto {

    private BigDecimal transferMoneyAmount;
    private long fromAccountId;
    private long toAccountId;

}
