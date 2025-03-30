package zycode.web.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import zycode.web.app.dto.CardDto;
import zycode.web.app.dto.TransferDto;
import zycode.web.app.entity.Card;
import zycode.web.app.entity.User;
import zycode.web.app.service.CardService;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @GetMapping
    public ResponseEntity<Card> getCard(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(cardService.getCard(user.getUid()));
    }

    @PostMapping("/apply")
    public ResponseEntity<?> createCard(@RequestBody CardDto cardDto, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        try {
            return ResponseEntity.ok(cardService.createCard(cardDto, user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong, failed to complete card application.");
        }
    }

    @PostMapping("/payment")
    public ResponseEntity<?> makePayment(@RequestBody TransferDto dto, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        try {
            return ResponseEntity.ok(cardService.makePayment(dto, user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong." + e.getMessage());
        }
    }


}
