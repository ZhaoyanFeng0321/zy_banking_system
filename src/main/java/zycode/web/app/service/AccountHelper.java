package zycode.web.app.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import zycode.web.app.dto.AccountDto;
import zycode.web.app.entity.Account;
import zycode.web.app.entity.User;
import zycode.web.app.repository.AccountRepository;
import zycode.web.app.util.RandomUtil;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Getter
public class AccountHelper {
    private final Logger logger = LoggerFactory.getLogger(AccountHelper.class);
    private final AccountRepository accountRepository;

    private final Map<String, String> CURRENCIES = Map.of(
            "USD", "United States Dollar",
            "CNY", "Chinese Yuan",
            "EUR", "Euro",
            "GBP", "British Pound",
            "JPY", "Japanese Yen",
            "NGN", "Nigerian Naira",
            "INR", "Indian Rupee"
    );

    public Account createAccount(AccountDto accountDto, User user) throws Exception {
        long accountNumber;
        try {
            validateAccountNonExistsForUser(accountDto.getCode(), user.getUid());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
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

    /**
     * Validates that an account of the given currency type does not already exist for the specified user.
     *
     * @param code The currency code of the account to be validated.
     * @param uid The unique identifier of the user for whom the account is being validated.
     * @throws Exception If an account of the given currency type already exists for the specified user.
     */
    private void validateAccountNonExistsForUser(String code, String uid) throws Exception {
        if(accountRepository.existsByCodeAndOwnerUid(code, uid)) {
            throw new Exception("Account of this type already exists for this user");
        }
    }
}
