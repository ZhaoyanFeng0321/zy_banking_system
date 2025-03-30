package zycode.web.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zycode.web.app.dto.AccountDto;
import zycode.web.app.dto.TransferDto;
import zycode.web.app.entity.Account;
import zycode.web.app.entity.Transaction;
import zycode.web.app.entity.User;
import zycode.web.app.repository.AccountRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountHelper accountHelper;
    private final ExchangeRateService exchangeRateService;

    public Account createAccount(AccountDto accountDto, User user) throws Exception {
        return accountHelper.createAccount(accountDto, user);
    }

    public List<Account> getUserAccounts(String uid) {
        return accountRepository.findAllByOwnerUid(uid);
    }

    public Account getAccountById(String accountId, String uid) throws Exception {
        if (!accountRepository.existsByAccountIdAndOwnerUid(accountId, uid)){
            throw new IllegalAccessException("Access forbidden");
        }
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new Exception("Account not found"));
    }

    public Transaction transferFunds(TransferDto transferDto, User user) throws Exception {
        var senderAccount = accountRepository.findByCodeAndOwnerUid(transferDto.getCode(), user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("Account of type currency do not exists for user"));
        var receiverAccount = accountRepository.findByAccountNumber(transferDto.getRecipientAccountNumber())
                .orElseThrow(() -> new UnsupportedOperationException("Recipient account does not exist"));
        return accountHelper.performTransfer(senderAccount, receiverAccount, transferDto.getAmount(), user);
    }

    public Transaction payCardBalance(TransferDto dto, User user) throws Exception {
        var senderAccount = accountRepository.findByCodeAndOwnerUid(dto.getCode(), user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("Account of type currency do not exists for user"));

        return accountHelper.payCardBalance(senderAccount, dto.getAmount(), user);
    }

    public Map<String, Double> getExchangeRate() {
        return exchangeRateService.getRates();
    }
}

