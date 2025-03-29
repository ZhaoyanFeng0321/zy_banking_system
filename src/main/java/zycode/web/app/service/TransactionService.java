package zycode.web.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zycode.web.app.entity.*;
import zycode.web.app.repository.TransactionRepository;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public Transaction createAccountTransaction(double amount, Type type, double txFee, User user, Account account) {
        var transaction = Transaction.builder()
                .amount(amount)
                .txFee(txFee)
                .status(Status.COMPLETED)
                .type(type)
                .owner(user)
                .account(account)
                .build();
        return  transactionRepository.save(transaction);
    }

}
