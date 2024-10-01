package io.codingogo.redissondemo.controller;

import io.codingogo.redissondemo.entity.Account;
import io.codingogo.redissondemo.exception.AccountNotFoundException;
import io.codingogo.redissondemo.model.CreateAccountRequest;
import io.codingogo.redissondemo.model.DepositRequest;
import io.codingogo.redissondemo.model.WithdrawRequest;
import io.codingogo.redissondemo.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Validated
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        Account account = accountService.createAccount(request.getOwner(), request.getInitialBalance());
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable Long accountId) throws AccountNotFoundException {
        Account account = accountService.getAccount(accountId);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<String> deposit(
            @PathVariable Long accountId,
            @RequestBody @Validated DepositRequest request) {
        accountService.deposit(accountId, request.getAmount());
        return ResponseEntity.ok("Deposit successful");
    }

    public ResponseEntity<String> withdraw(
            @PathVariable Long accountId,
            @RequestBody @Validated WithdrawRequest request) {
        accountService.withdraw(accountId, request.getAmount());
        return ResponseEntity.ok("Withdrawal successful");
    }
}

