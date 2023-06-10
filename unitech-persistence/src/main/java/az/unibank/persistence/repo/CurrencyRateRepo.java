package az.unibank.persistence.repo;

import az.unibank.persistence.domains.CurrencyRate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRateRepo extends JpaRepository<CurrencyRate, Long> {

    @EntityGraph(attributePaths = {"fromCurrency", "toCurrency"}, type = EntityGraph.EntityGraphType.FETCH)
    Optional<CurrencyRate> findByFromCurrencyIdAndToCurrencyId(long fromCurrencyId, long toCurrencyId);

}
