package az.unibank.unitechapp.services.impl;

import az.unibank.commons.dto.AccountDto;
import az.unibank.commons.dto.Result;
import az.unibank.commons.dto.TransferMoneyRequestDto;
import az.unibank.commons.util.security.SecurityUtils;
import az.unibank.persistence.domains.Account;
import az.unibank.persistence.repo.AccountRepository;
import az.unibank.unitechapp.mapper.AccountMapper;
import az.unibank.unitechapp.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static az.unibank.commons.enums.ResponseCode.*;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {


    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final SecurityUtils securityUtils;


    @Override
    public List<AccountDto> getAllActiveUserAccounts() {
        long currentUserFromSession = securityUtils.getCurrentUser();
        List<Account> allAccountsByUserId = accountRepository.findAllActiveAccountsByUserId(currentUserFromSession);

        return accountMapper.mapToDtoList(allAccountsByUserId);
    }

    @Transactional
    @Override
    public Object transferMoneyToMyAnotherAccount(TransferMoneyRequestDto transferMoneyRequestDto) {
        long currentUserFromSession = securityUtils.getCurrentUser();

        Optional<Account> fromAccountOptional = accountRepository.findAccountByIdAndUserId(transferMoneyRequestDto.getFromAccountId(), currentUserFromSession);

        Optional<Account> toAccountOptional = accountRepository.findAccountByIdAndUserId(transferMoneyRequestDto.getToAccountId(), currentUserFromSession);

        Object responseFromCheckOnExistAccounts = checkOnExistAccounts(fromAccountOptional, toAccountOptional, transferMoneyRequestDto);

        if (responseFromCheckOnExistAccounts != null)
            return responseFromCheckOnExistAccounts;

        Account toAccount = toAccountOptional.get();
        Account fromAccount = fromAccountOptional.get();
        BigDecimal transferMoneyAmount = transferMoneyRequestDto.getTransferMoney();

        if (fromAccount.getBalance().compareTo(transferMoneyAmount) == -1)
            return Result.Builder().response(NOT_ENOUGH_BALANCE)
                    .add("message", "You do not have enough money on your balance.")
                    .build();

        fromAccount.setBalance(fromAccount.getBalance().subtract(transferMoneyAmount));
        toAccount.setBalance(toAccount.getBalance().add(transferMoneyAmount));

        return Result.Builder().response(OK)
                .build();
    }


    private Object checkOnExistAccounts(Optional<Account> fromAccount, Optional<Account> toAccount, TransferMoneyRequestDto transferMoneyRequestDto) {
        if (fromAccount.isEmpty())
            return Result.Builder().response(INCORRECT_VALUE)
                    .add("message", String.format("Account with id (%s) not found for your user.", transferMoneyRequestDto.getFromAccountId()))
                    .build();
        else if (!fromAccount.get().isActive())
            return Result.Builder().response(INCORRECT_VALUE)
                    .add("message", String.format("Account with id (%s) is not active.", transferMoneyRequestDto.getFromAccountId()))
                    .build();
        else if (fromAccount.get().getEndDate().isBefore(LocalDate.now()))
            return Result.Builder().response(INCORRECT_VALUE)
                    .add("message", String.format("Account with id (%s) is expired.", transferMoneyRequestDto.getFromAccountId()))
                    .build();

        if (toAccount.isEmpty())
            return Result.Builder().response(INCORRECT_VALUE)
                    .add("message", String.format("Account with id (%s) not found for your user.", transferMoneyRequestDto.getToAccountId()))
                    .build();
        else if (!toAccount.get().isActive())
            return Result.Builder().response(INCORRECT_VALUE)
                    .add("message", String.format("Account with id (%s) is not active.", transferMoneyRequestDto.getToAccountId()))
                    .build();
        else if (toAccount.get().getEndDate().isBefore(LocalDate.now()))
            return Result.Builder().response(INCORRECT_VALUE)
                    .add("message", String.format("Account with id (%s) is expired.", transferMoneyRequestDto.getToAccountId()))
                    .build();

        if (transferMoneyRequestDto.getFromAccountId() == transferMoneyRequestDto.getToAccountId())
            return Result.Builder().response(INCORRECT_VALUE)
                    .add("message", "You cannot send money to the same account number.")
                    .build();

        return null;
    }


}
