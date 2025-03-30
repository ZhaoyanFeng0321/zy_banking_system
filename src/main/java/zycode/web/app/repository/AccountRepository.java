package zycode.web.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zycode.web.app.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findAllByOwnerUid(String uid);

    boolean existsByAccountNumber(long accountNumber);

    boolean existsByCodeAndOwnerUid(String code, String uid);

    /**
     * Retrieves the account with the given code and owner UID.
     *
     * @param code the code to search for
     * @param uid the owner UID to search for
     * @return an {@link Optional} containing the account with the given code and owner UID,
     * or an empty {@link Optional} if no such account exists
     */
    Optional<Account> findByCodeAndOwnerUid(String code, String uid);

    Optional<Account> findByAccountNumber(long recipientAccountNumber);

    boolean existsByAccountIdAndOwnerUid(String accountId, String uid);
}
