package az.unibank.commons.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyDto {

    private long id;
    private String currency;

}
