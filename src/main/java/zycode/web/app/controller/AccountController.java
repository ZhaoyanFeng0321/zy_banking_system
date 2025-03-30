package zycode.web.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import zycode.web.app.dto.AccountDto;
import zycode.web.app.dto.TransferDto;
import zycode.web.app.entity.Account;
import zycode.web.app.entity.User;
import zycode.web.app.service.AccountService;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/list")
    public ResponseEntity<List<Account>> getUserAccounts(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(accountService.getUserAccounts(user.getUid()));
    }

    @GetMapping
    public ResponseEntity<?> getUserAccountById(@RequestParam String accountId, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        try {
            return ResponseEntity.ok(accountService.getAccountById(accountId, user.getUid()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Something went wrong." + e.getMessage());
        }
    }

    /**
     * Transfers funds from one account to another for the authenticated user.
     *
     * @param transferDto The details of the transfer.
     * @param authentication The current authentication context.
     * @return The created transaction.
     */
    @PostMapping("/transfer")
    public ResponseEntity<?> transferAccount(@RequestBody TransferDto transferDto, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        try {
            return ResponseEntity.ok(accountService.transferFunds(transferDto, user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong, failed to make transfer.");
        }
    }

    @PostMapping("/payCard")
    public ResponseEntity<?> payCardBalance(@RequestBody TransferDto dto, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        try {
            return ResponseEntity.ok(accountService.payCardBalance(dto, user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong." + e.getMessage());
        }
    }

    /**
     * Retrieves the current exchange rates for supported currencies.
     *
     * @return The map of currency codes to exchange rates.
     */
    @GetMapping("/rates")
    public ResponseEntity<Map<String, Double>> getExchangeRate() {
        return ResponseEntity.ok(accountService.getExchangeRate());
    }


}
