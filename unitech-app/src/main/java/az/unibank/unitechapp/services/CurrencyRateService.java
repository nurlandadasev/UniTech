package az.unibank.unitechapp.services;


import java.math.BigDecimal;

public interface CurrencyRateService {

    BigDecimal getCurrentCurrencyRate(long fromCurrencyId, long toCurrencyId);

    Object getCurrentCurrencyRateForApi(long fromCurrencyId, long toCurrencyId);


}
