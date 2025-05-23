package zycode.web.app.service.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import zycode.web.app.dto.AccountDto;
import zycode.web.app.entity.*;
import zycode.web.app.repository.AccountRepository;
import zycode.web.app.repository.CardRepository;
import zycode.web.app.service.TransactionService;
import zycode.web.app.util.RandomUtil;

import javax.naming.OperationNotSupportedException;
import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Getter
public class AccountHelper {
    private final Logger logger = LoggerFactory.getLogger(AccountHelper.class);
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final CardRepository cardRepository;


    private final Map<String, String> CURRENCIES = Map.of(
            "USD", "United States Dollar",
            "CNY", "Chinese Yuan",
            "EUR", "Euro",
            "GBP", "British Pound",
            "JPY", "Japanese Yen",
            "NGN", "Nigerian Naira",
            "INR", "Indian Rupee"
    );

    public Account createAccount(AccountDto accountDto, User user) throws OperationNotSupportedException {
        long accountNumber;
        validateAccountNonExistsForUser(accountDto.getCode(), user.getUid());

        do {
            accountNumber = new RandomUtil().generateRandom(10);
        } while(accountRepository.existsByAccountNumber(accountNumber));

        var account = Account.builder()
                .accountNumber(accountNumber)
                .accountName(user.getFirstname() + " " + user.getLastname())
                .balance(1000)
                .owner(user)
                .code(accountDto.getCode())
                .symbol(accountDto.getSymbol())
                .label(CURRENCIES.get(accountDto.getCode()))
                .build();
        return accountRepository.save(account);
    }

    public Transaction performTransfer(Account senderAccount, Account receiverAccount, double amount, User user) throws OperationNotSupportedException {
        validateSufficientFunds(senderAccount, amount);
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);
        // update account balance
        accountRepository.saveAll(List.of(senderAccount, receiverAccount));
        // create transaction record for sender and receiver
        var senderTransaction = transactionService.createAccountTransaction(amount, Type.WITHDRAW, 0, user, senderAccount);
        var receiverTransaction = transactionService.createAccountTransaction(amount, Type.DEPOSIT, 0, receiverAccount.getOwner(), receiverAccount);

        return senderTransaction;
    }

    public Transaction payCardBalance(Account senderAccount, double amount, User user) throws Exception {
        var card = cardRepository.findByOwnerUid(user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("User does not have card"));

        validateSufficientFunds(senderAccount, amount);
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        card.setBalance(card.getBalance() + amount);
        cardRepository.save(card);
        accountRepository.save(senderAccount);
        return transactionService.payCardBalance(amount, 0, user, card, senderAccount);
    }

    /**
     * Validates that an account of the given currency type does not already exist for the specified user.
     *
     * @param code The currency code of the account to be validated.
     * @param uid The unique identifier of the user for whom the account is being validated.
     * @throws OperationNotSupportedException If an account of the given currency type already exists for the specified user.
     */
    private void validateAccountNonExistsForUser(String code, String uid) throws OperationNotSupportedException {
        if(accountRepository.existsByCodeAndOwnerUid(code, uid)) {
            throw new OperationNotSupportedException("Account of this type already exists for this user");
        }
    }

    public void validateSufficientFunds(Account account, double amount) throws OperationNotSupportedException {
        if (amount <= 0) {
            throw new OperationNotSupportedException("Invalid amount to transfer");
        }
        if(account.getBalance() < amount) {
            throw new OperationNotSupportedException("Insufficient funds in the account");
        }
    }
}
