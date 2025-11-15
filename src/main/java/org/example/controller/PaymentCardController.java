package org.example.controller;


import jakarta.validation.Valid;
import org.example.DTO.PaymentCardDTO;
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
    public ResponseEntity<PaymentCardDTO> createCard(@Valid @RequestBody PaymentCardDTO cardDTO) {
        PaymentCardDTO created = cardService.createCard(cardDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardDTO> getCardById(@PathVariable Integer id) {
        PaymentCardDTO card = cardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentCardDTO>> getCardsByUser(@PathVariable Integer userId) {
        List<PaymentCardDTO> cards = cardService.getCardsByUser(userId);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardDTO> updateCard(@PathVariable Integer id,
                                                     @Valid @RequestBody PaymentCardDTO cardDTO) {
        PaymentCardDTO updated = cardService.updateCard(id, cardDTO);
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
