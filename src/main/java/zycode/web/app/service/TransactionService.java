package zycode.web.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zycode.web.app.entity.*;
import zycode.web.app.repository.TransactionRepository;

import java.util.List;

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

    public Transaction makeCardPayment(double amount, double txFee, User user, Card card, String receiverAccount) {
        var transaction = Transaction.builder()
                .amount(amount)
                .type(card.getType())
                .status(Status.COMPLETED)
                .txFee(txFee)
                .owner(user)
                .card(card)
                .receiver(receiverAccount)
                .build();
        return transactionRepository.save(transaction);
    }

    public Transaction payCardBalance(double amount, double txFee, User user, Card card, Account senderAccount) {
        var transaction = Transaction.builder()
                .amount(amount)
                .txFee(txFee)
                .status(Status.COMPLETED)
                .type(Type.WITHDRAW)
                .owner(user)
                .card(card)
                .account(senderAccount)
                .build();
        return  transactionRepository.save(transaction);
    }

    public List<Transaction> getUserTransactions(String uid) {
        return transactionRepository.findByUid(uid);
    }
}
