package zycode.web.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zycode.web.app.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
