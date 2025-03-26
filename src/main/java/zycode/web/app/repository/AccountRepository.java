package zycode.web.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zycode.web.app.entity.Account;

public interface AccountRepository extends JpaRepository<Account, String> {
}
