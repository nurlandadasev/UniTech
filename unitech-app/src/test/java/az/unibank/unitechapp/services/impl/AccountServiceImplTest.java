package az.unibank.unitechapp.services.impl;

import az.unibank.commons.dto.AccountDto;
import az.unibank.commons.dto.CurrencyDto;
import az.unibank.commons.dto.Result;
import az.unibank.commons.dto.TransferMoneyRequestDto;
import az.unibank.commons.util.security.SecurityUtils;
import az.unibank.persistence.domains.Account;
import az.unibank.persistence.domains.Currency;
import az.unibank.persistence.repo.AccountRepository;
import az.unibank.unitechapp.mapper.AccountMapper;
import az.unibank.unitechapp.services.CurrencyRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static az.unibank.commons.enums.ResponseCode.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository mockAccountRepository;
    @Mock
    private AccountMapper mockAccountMapper;
    @Mock
    private SecurityUtils mockSecurityUtils;
    @Mock
    private CurrencyRateService mockCurrencyRateService;

    @Captor
    private ArgumentCaptor<Account> accountManagerCaptor;
    @InjectMocks
    private AccountServiceImpl accountServiceImplUnderTest;

    @Test
    void testGetAllActiveUserAccounts() {
        // Setup
        when(mockSecurityUtils.getCurrentUser()).thenReturn(0L);

        final List<Account> accounts = List.of(Account.builder()
                .endDate(LocalDate.of(2020, 1, 1))
                .isActive(false)
                .balance(new BigDecimal("0.00"))
                .currency(Currency.builder()
                        .id(0L)
                        .build())
                .build());
        when(mockAccountRepository.findAllActiveAccountsByUserId(0L)).thenReturn(accounts);

        final List<AccountDto> accountsDtoResponse = List.of(AccountDto.builder()
                .endDate(LocalDate.of(2020, 1, 1))
                .balance(new BigDecimal("0.00"))
                .currency(CurrencyDto.builder()
                        .id(0L)
                        .build())
                .build());
        when(mockAccountMapper.mapToDtoList(accounts)).thenReturn(accountsDtoResponse);

        // Run the test
        final Object result = accountServiceImplUnderTest.getAllActiveUserAccounts();

        // Verify the results
        assertEquals(Result.Builder().response(OK)
                .add("allActiveAccounts", accountsDtoResponse)
                .build(), result);
    }

    @Test
    void testGetAllActiveUserAccounts_AccountRepositoryReturnsNoItems() {
        // Setup
        when(mockSecurityUtils.getCurrentUser()).thenReturn(0L);
        when(mockAccountRepository.findAllActiveAccountsByUserId(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final Object result = accountServiceImplUnderTest.getAllActiveUserAccounts();

        // Verify the results
        assertEquals(Result.Builder().response(OK)
                .add("allActiveAccounts", Collections.emptyList())
                .build(), result);
    }

    @Test
    void testTransferMoneyToMyAnotherAccount() {
        long fromAccountId = 1L;
        long toAccountId = 2L;
        BigDecimal transferAmount = new BigDecimal("100");
        // Setup
        final TransferMoneyRequestDto transferMoneyRequestDto = new TransferMoneyRequestDto(transferAmount, fromAccountId,
                toAccountId);

        long currentUserId = 2;
        when(mockSecurityUtils.getCurrentUser()).thenReturn(currentUserId);

        BigDecimal fromAccountBalance = new BigDecimal(300);
        final Optional<Account> fromAccount = Optional.of(Account.builder()
                .endDate(LocalDate.of(2023, 6, 29))
                .isActive(true)
                .balance(fromAccountBalance)
                .currency(Currency.builder()
                        .id(1L)
                        .currency("AZN")
                        .build())
                .build());

        BigDecimal toAccountBalance = new BigDecimal(200);

        final Optional<Account> toAccount = Optional.of(Account.builder()
                .endDate(LocalDate.of(2023, 6, 30))
                .isActive(true)
                .balance(toAccountBalance)
                .currency(Currency.builder()
                        .id(2L)
                        .currency("USD")
                        .build())
                .build());

        when(mockAccountRepository.findAccountByIdAndUserId(1L, currentUserId)).thenReturn(fromAccount);
        when(mockAccountRepository.findAccountByIdAndUserId(2L, currentUserId)).thenReturn(toAccount);

        BigDecimal currencyRateValue = new BigDecimal("0.67");
        when(mockCurrencyRateService.getCurrentCurrencyRate(fromAccount.get().getCurrency().getId(),
                toAccount.get().getCurrency().getId())).thenReturn(currencyRateValue);

        // Run the test
        final Object result = accountServiceImplUnderTest.transferMoneyToMyAnotherAccount(transferMoneyRequestDto);

        then(mockAccountRepository).should(atLeast(2)).save(accountManagerCaptor.capture());

        List<Account> allCapturedValues = accountManagerCaptor.getAllValues();

        assertEquals(Account.builder()
                        .endDate(LocalDate.of(2023, 6, 29))
                        .isActive(true)
                        .balance(fromAccountBalance.subtract(transferAmount))
                        .currency(Currency.builder()
                                .id(1L)
                                .currency("AZN")
                                .build())
                        .build(),
                allCapturedValues.get(0));

        assertEquals(Account.builder()
                        .endDate(LocalDate.of(2023, 6, 30))
                        .isActive(true)
                        .balance(toAccountBalance.add(transferAmount.multiply(currencyRateValue)))
                        .currency(Currency.builder()
                                .id(2L)
                                .currency("USD")
                                .build())
                        .build(),
                allCapturedValues.get(1));


    }

}
