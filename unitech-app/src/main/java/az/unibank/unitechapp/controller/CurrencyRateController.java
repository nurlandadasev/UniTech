package az.unibank.unitechapp.controller;


import az.unibank.unitechapp.services.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/currency-rate")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;

    @GetMapping("/from/{fromCurrencyId}/to/{toCurrencyId}")
    public ResponseEntity<Object> getCurrentCurrencyRate(@PathVariable long fromCurrencyId, @PathVariable long toCurrencyId){
        return ResponseEntity.ok(currencyRateService.getCurrentCurrencyRateForApi(fromCurrencyId, toCurrencyId));
    }


}
