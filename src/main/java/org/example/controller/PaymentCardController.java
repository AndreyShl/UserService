package org.example.controller;


import jakarta.validation.Valid;
import org.example.dto.PaymentCarddto;
import org.example.service.PaymentCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@Validated
public class PaymentCardController {

    private final PaymentCardService cardService;

    public PaymentCardController(PaymentCardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<PaymentCarddto> createCard(@Valid @RequestBody PaymentCarddto cardDTO) {
        PaymentCarddto created = cardService.createCard(cardDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PaymentCarddto> getCardById(@PathVariable Integer id) {
        PaymentCarddto card = cardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentCarddto>> getCardsByUser(@PathVariable Integer userId) {
        List<PaymentCarddto> cards = cardService.getCardsByUser(userId);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCarddto> updateCard(@PathVariable Integer id,
                                                     @Valid @RequestBody PaymentCarddto cardDTO) {
        PaymentCarddto updated = cardService.updateCard(id, cardDTO);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateCard(@PathVariable Integer id) {
        cardService.activateCard(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateCard(@PathVariable Integer id) {
        cardService.deactivateCard(id);
        return ResponseEntity.ok().build();
    }
}
