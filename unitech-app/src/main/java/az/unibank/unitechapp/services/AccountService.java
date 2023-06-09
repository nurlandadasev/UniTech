package az.unibank.unitechapp.services;

import az.unibank.commons.dto.AccountDto;
import az.unibank.commons.dto.TransferMoneyRequestDto;

import java.util.List;

public interface AccountService {

    Object getAllActiveUserAccounts();

    Object transferMoneyToMyAnotherAccount(TransferMoneyRequestDto transferMoneyRequestDto);


}
