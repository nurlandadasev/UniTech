package az.unibank.unitechapp.services.impl;

import az.unibank.commons.dto.Result;
import az.unibank.persistence.domains.CurrencyRate;
import az.unibank.persistence.repo.CurrencyRateRepo;
import az.unibank.unitechapp.services.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

import static az.unibank.commons.config.Constants.RESPONSE_FULL_MESSAGE;
import static az.unibank.commons.enums.ResponseCode.INVALID_VALUE;
import static az.unibank.commons.enums.ResponseCode.OK;

@Log4j2
@RequiredArgsConstructor
@Service
public class CurrencyRateServiceImpl implements CurrencyRateService {

    private final CurrencyRateRepo currencyRateRepo;

    /**
     * This method get current currency rate value.
     * If 60 seconds have passed since the last update, then it will update the course in our database otherwise return from our DB.
     * @return Result after exchange
     * @throws UnsupportedOperationException - if currency rate not found in DB by fromCurrencyId and toCurrencyId
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public BigDecimal getCurrentCurrencyRate(long fromCurrencyId, long toCurrencyId) {
        Optional<CurrencyRate> currencyRate = currencyRateRepo.findByFromCurrencyIdAndToCurrencyId(
                fromCurrencyId, toCurrencyId);

        if (currencyRate.isEmpty())
            throw new UnsupportedOperationException("This currency not supported");

        CurrencyRate currentCurrencyRate = currencyRate.get();

        if (ChronoUnit.SECONDS.between(currentCurrencyRate.getLastUpdatedDateTime(), LocalDateTime.now()) > 60) {
            BigDecimal updatedCurrencyRateValue = mockedMethodForGetCurrencyValue(currentCurrencyRate.getCurrencyRateValue());
            currentCurrencyRate.setCurrencyRateValue(updatedCurrencyRateValue);
            currentCurrencyRate.setLastUpdatedDateTime(LocalDateTime.now());
            currencyRateRepo.save(currentCurrencyRate);
            return updatedCurrencyRateValue;
        } else {
            return currentCurrencyRate.getCurrencyRateValue();
        }
    }

    @Override
    public Object getCurrentCurrencyRateForApi(long fromCurrencyId, long toCurrencyId) {
        try {
            BigDecimal currentCurrencyRate = getCurrentCurrencyRate(fromCurrencyId, toCurrencyId);
            return Result.Builder().response(OK)
                    .add("currencyRate", currentCurrencyRate)
                    .build();
        } catch (UnsupportedOperationException e) {
            log.error("<getCurrentCurrencyRateForApi> Message: " + e.getLocalizedMessage());
            return Result.Builder().response(INVALID_VALUE)
                    .add(RESPONSE_FULL_MESSAGE, e.getLocalizedMessage())
                    .build();
        }

    }


    /**
     * Mocked method for get current currency from third party service.
     * This method return random value by currency for the imitation third party currency service.
     *
     * @param currentCurrencyInDB - only to generate a random number close to this currentCurrencyInDB.
     */
    private BigDecimal mockedMethodForGetCurrencyValue(BigDecimal currentCurrencyInDB) {
        return getRandomNumberByRange(currentCurrencyInDB, currentCurrencyInDB.add(BigDecimal.valueOf(0.15)));
    }

    private BigDecimal getRandomNumberByRange(BigDecimal min, BigDecimal max) {
        Random random = new Random();
        BigDecimal randomNumber = min.add(max.subtract(min).multiply(BigDecimal.valueOf(random.nextDouble())));
        randomNumber = randomNumber.setScale(2, RoundingMode.HALF_UP);

        return randomNumber;
    }


}
