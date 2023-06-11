package az.unibank.unitechapp.services.impl;

import az.unibank.commons.dto.AccountDto;
import az.unibank.commons.dto.Result;
import az.unibank.commons.dto.TransferMoneyRequestDto;
import az.unibank.commons.util.security.SecurityUtils;
import az.unibank.persistence.domains.Account;
import az.unibank.persistence.repo.AccountRepository;
import az.unibank.unitechapp.mapper.AccountMapper;
import az.unibank.unitechapp.services.AccountService;
import az.unibank.unitechapp.services.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static az.unibank.commons.config.Constants.RESPONSE_FULL_MESSAGE;
import static az.unibank.commons.enums.ResponseCode.*;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {


    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final SecurityUtils securityUtils;
    private final CurrencyRateService currencyRateService;

    /**
     * Get all active accounts
     */
    @Override
    public Object getAllActiveUserAccounts() {
        long currentUserFromSession = securityUtils.getCurrentUser();
        List<Account> allAccountsByUserId = accountRepository.findAllActiveAccountsByUserId(currentUserFromSession);
        List<AccountDto> accountDtos = accountMapper.mapToDtoList(allAccountsByUserId);
        return Result.Builder().response(OK)
                .add("allActiveAccounts", accountDtos)
                .build();
    }

    /**
     * Method for transfer money between accounts.
     */
    @Transactional
    @Override
    public Object transferMoneyToMyAnotherAccount(TransferMoneyRequestDto transferMoneyRequestDto) {
        long currentUserFromSession = securityUtils.getCurrentUser();

        Optional<Account> fromAccountOptional = accountRepository.findAccountByIdAndUserId(transferMoneyRequestDto.getFromAccountId(), currentUserFromSession);

        Optional<Account> toAccountOptional = accountRepository.findAccountByIdAndUserId(transferMoneyRequestDto.getToAccountId(), currentUserFromSession);

        Object responseFromCheckOnExistAccounts = checkOnExistAccounts(fromAccountOptional, toAccountOptional, transferMoneyRequestDto);

        if (responseFromCheckOnExistAccounts != null)
            return responseFromCheckOnExistAccounts;

        Account fromAccount = fromAccountOptional.get();
        Account toAccount = toAccountOptional.get();

        BigDecimal transferMoneyAmount = transferMoneyRequestDto.getTransferMoneyAmount();

        if (fromAccount.getBalance().compareTo(transferMoneyAmount) == -1)
            return Result.Builder().response(NOT_ENOUGH_BALANCE)
                    .add(RESPONSE_FULL_MESSAGE, "You do not have enough money on your balance.")
                    .build();

        fromAccount.setBalance(fromAccount.getBalance().subtract(transferMoneyAmount));

        BigDecimal currentCurrencyRate = currencyRateService.getCurrentCurrencyRate(fromAccount.getCurrency().getId(), toAccount.getCurrency().getId());

        toAccount.setBalance(toAccount.getBalance().add(transferMoneyAmount.multiply(currentCurrencyRate)));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return Result.Builder().response(OK)
                .build();
    }


    private Object checkOnExistAccounts(Optional<Account> fromAccount, Optional<Account> toAccount, TransferMoneyRequestDto transferMoneyRequestDto) {
        if (fromAccount.isEmpty())
            return Result.Builder().response(INCORRECT_VALUE)
                    .add(RESPONSE_FULL_MESSAGE, String.format("Account with id (%s) not found for your user.", transferMoneyRequestDto.getFromAccountId()))
                    .build();
        else if (!fromAccount.get().isActive())
            return Result.Builder().response(INCORRECT_VALUE)
                    .add(RESPONSE_FULL_MESSAGE, String.format("Account with id (%s) is not active.", transferMoneyRequestDto.getFromAccountId()))
                    .build();
        else if (fromAccount.get().getEndDate().isBefore(LocalDate.now()))
            return Result.Builder().response(INCORRECT_VALUE)
                    .add(RESPONSE_FULL_MESSAGE, String.format("Account with id (%s) is expired.", transferMoneyRequestDto.getFromAccountId()))
                    .build();

        if (toAccount.isEmpty())
            return Result.Builder().response(INCORRECT_VALUE)
                    .add(RESPONSE_FULL_MESSAGE, String.format("Account with id (%s) not found for your user.", transferMoneyRequestDto.getToAccountId()))
                    .build();
        else if (!toAccount.get().isActive())
            return Result.Builder().response(INCORRECT_VALUE)
                    .add(RESPONSE_FULL_MESSAGE, String.format("Account with id (%s) is not active.", transferMoneyRequestDto.getToAccountId()))
                    .build();
        else if (toAccount.get().getEndDate().isBefore(LocalDate.now()))
            return Result.Builder().response(INCORRECT_VALUE)
                    .add(RESPONSE_FULL_MESSAGE, String.format("Account with id (%s) is expired.", transferMoneyRequestDto.getToAccountId()))
                    .build();

        if (transferMoneyRequestDto.getFromAccountId() == transferMoneyRequestDto.getToAccountId())
            return Result.Builder().response(INCORRECT_VALUE)
                    .add(RESPONSE_FULL_MESSAGE, "You cannot send money to the same account number.")
                    .build();

        return null;
    }


}
