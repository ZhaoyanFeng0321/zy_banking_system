package zycode.web.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import zycode.web.app.dto.AccountDto;
import zycode.web.app.dto.TransferDto;
import zycode.web.app.entity.Account;
import zycode.web.app.entity.Transaction;
import zycode.web.app.entity.User;
import zycode.web.app.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    /**
     * Creates a new account for the authenticated user.
     *
     * @param accountDto The details of the new account.
     * @param authentication The current authentication context.
     * @return The created account.
     */
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountDto accountDto, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        try {
            Account account = accountService.createAccount(accountDto, user);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong, failed to create account.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(accountService.getUserAccounts(user.getUid()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferAccount(@RequestBody TransferDto transferDto, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        try {
            return ResponseEntity.ok(accountService.transferFunds(transferDto, user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong, failed to make transfer.");
        }
    }

}
