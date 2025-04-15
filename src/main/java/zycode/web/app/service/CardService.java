package zycode.web.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zycode.web.app.dto.CardDto;
import zycode.web.app.entity.*;
import zycode.web.app.repository.CardRepository;
import zycode.web.app.util.RandomUtil;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {
    private final CardRepository cardRepository;
    private final TransactionService transactionService;

    public Card getCard(String uid) {
        return cardRepository.findByOwnerUid(uid).orElseThrow(() ->
                new UnsupportedOperationException("User does not have a card"));
    }

    public Card createCard(CardDto cardDto, User user) throws Exception {
        if (cardRepository.existsByOwnerUid(user.getUid())) {
            throw new UnsupportedOperationException("Card already exists for this user");
        }
        long cardNumber;
        do {
            cardNumber = new RandomUtil().generateRandom(16);
        } while(cardRepository.existsByCardNumber(cardNumber));

        var card = Card.builder()
                .cardNumber(cardNumber)
                .cardHolder(user.getFirstname() + " " + user.getLastname())
                .balance(cardDto.getBalance())
                .creditLine(cardDto.getCreditLine())
                .type(Type.valueOf(cardDto.getType().toUpperCase()))
                .billingAddress(cardDto.getBillingAddress())
                .exp(LocalDateTime.now().plusYears(3))
                .owner(user)
                .cvv(new RandomUtil().generateRandom(3).toString())
                .build();
        return cardRepository.save(card);
    }


    public Transaction makePayment(String destinationAccount, double amount, User user) throws Exception {
        if (amount <= 0) throw new UnsupportedOperationException("Payment is not supported. Invalid amount.");

        var card = cardRepository.findByOwnerUid(user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("User does not have a card"));
        exceedCreditLimit(card, amount);
        card.setBalance(card.getBalance() + amount);
        cardRepository.save(card);
        return transactionService
                .makeCardPayment(amount,0, user, card, destinationAccount);
    }

    private void exceedCreditLimit(Card card, double amount) throws Exception {
        if (card.getBalance() + amount > card.getCreditLine()) {
            throw new UnsupportedOperationException("Payment not allowed. Credit limit exceeded.");
        }
    }
}
