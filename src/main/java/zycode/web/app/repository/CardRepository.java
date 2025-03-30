package zycode.web.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zycode.web.app.entity.Card;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, String> {
    Optional<Card> findByOwnerUid(String uid);

    boolean existsByOwnerUid(String uid);

    boolean existsByCardNumber(long cardNumber);
}
