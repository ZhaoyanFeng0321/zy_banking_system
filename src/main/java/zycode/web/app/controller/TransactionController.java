package zycode.web.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zycode.web.app.entity.Transaction;
import zycode.web.app.entity.User;
import zycode.web.app.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getUserTransactions(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(transactionService.getUserTransactions(user.getUid()));
    }

}
