package zycode.web.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zycode.web.app.dto.AccountDto;
import zycode.web.app.dto.TransferDto;
import zycode.web.app.entity.Account;
import zycode.web.app.entity.Transaction;
import zycode.web.app.entity.User;
import zycode.web.app.repository.AccountRepository;
import zycode.web.app.repository.CardRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountHelper accountHelper;
    private final ExchangeRateService exchangeRateService;
    private final CardRepository cardRepository;


    public Account createAccount(AccountDto accountDto, User user) throws Exception {
        return accountHelper.createAccount(accountDto, user);
    }

    public List<Account> getUserAccounts(String uid) {
        return accountRepository.findAllByOwnerUid(uid);
    }


    public Transaction transferFunds(TransferDto transferDto, User user) throws Exception {
        var senderAccount = accountRepository.findByCodeAndOwnerUid(transferDto.getCode(), user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("Account of type currency do not exists for user"));
        var receiverAccount = accountRepository.findByAccountNumber(transferDto.getRecipientAccountNumber())
                .orElseThrow(() -> new UnsupportedOperationException("Recipient account does not exist"));
        return accountHelper.performTransfer(senderAccount, receiverAccount, transferDto.getAmount(), user);
    }

    public Transaction payCardBalance(TransferDto dto, User user) throws Exception {
        var card = cardRepository.findByOwnerUid(user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("User does not have card"));
        var senderAccount = accountRepository.findByCodeAndOwnerUid(dto.getCode(), user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("Account of type currency do not exists for user"));

        return accountHelper.payCardBalance(senderAccount, card, dto.getAmount(), user);
    }

    public Map<String, Double> getExchangeRate() {
        return exchangeRateService.getRates();
    }
}

