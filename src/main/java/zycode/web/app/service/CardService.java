package zycode.web.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zycode.web.app.dto.CardDto;
import zycode.web.app.dto.TransferDto;
import zycode.web.app.entity.*;
import zycode.web.app.repository.AccountRepository;
import zycode.web.app.repository.CardRepository;
import zycode.web.app.util.RandomUtil;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final TransactionService transactionService;



    public Card getCard(String uid) {
        return cardRepository.findByOwnerUid(uid).orElseThrow();
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


    public Transaction makePayment(TransferDto dto, User user) throws Exception {
        var card = cardRepository.findByOwnerUid(user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("User does not have card"));
        if(dto.getAmount() <= 0) throw new UnsupportedOperationException("Invalid amount to transfer");
        card.setBalance(card.getBalance() - dto.getAmount());
        cardRepository.save(card);
        return transactionService
                .makeCardPayment(dto.getAmount(),0, user, card, dto.getReceiver());
    }
}
