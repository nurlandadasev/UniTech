package az.unibank.unitechapp.controller;

import az.unibank.commons.dto.TransferMoneyRequestDto;
import az.unibank.unitechapp.services.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<Object> getAllActiveAccountsByUserId() {
        return ResponseEntity.ok(accountService.getAllActiveUserAccounts());

    }

    @PutMapping("/transfer-money")
    public ResponseEntity<Object> transferMoneyToMyAnotherAccount(@RequestBody TransferMoneyRequestDto transferMoneyRequestDto) {
        return ResponseEntity.ok(accountService.transferMoneyToMyAnotherAccount(transferMoneyRequestDto));
    }

}
