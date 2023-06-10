package az.unibank.persistence.domains;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder
@Entity
@Table(name = "currency_rate")
public class CurrencyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_currency_fk", nullable = false)
    private Currency fromCurrency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_currency_fk", nullable = false)
    private Currency toCurrency;

    @Column(name = "currency_rate_value")
    private BigDecimal currencyRateValue;

    private LocalDateTime lastUpdatedDateTime;






}
