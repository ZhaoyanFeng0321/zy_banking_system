package zycode.web.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zycode.web.app.entity.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByOwnerUid(String uid);
}
