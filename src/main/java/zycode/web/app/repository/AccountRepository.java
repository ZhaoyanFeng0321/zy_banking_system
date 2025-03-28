package zycode.web.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zycode.web.app.entity.Account;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findAllByOwnerUid(String uid);

    boolean existsByAccountNumber(long accountNumber);

    boolean existsByCodeAndOwnerUid(String code, String uid);
}
