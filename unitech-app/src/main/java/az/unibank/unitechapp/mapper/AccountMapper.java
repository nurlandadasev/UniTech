package az.unibank.unitechapp.mapper;

import az.unibank.commons.dto.AccountDto;
import az.unibank.persistence.domains.Account;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDto mapToDto (Account account);

    List<AccountDto> mapToDtoList(List<Account> accountList);


}
