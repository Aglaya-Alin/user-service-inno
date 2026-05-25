package com.example.user_service_inno.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service_inno.api.dto.PaymentCardDTO;
import com.example.user_service_inno.service.PaymentCardService;

import jakarta.validation.Valid;

@RequestMapping("/cards")
@RestController
public class PaymentCardController {
    @Autowired private PaymentCardService paymentCardService;

    @DeleteMapping("/{card_id}")
    public ResponseEntity<PaymentCardDTO> deleteCard(@PathVariable("card_id") UUID cardId) {
        paymentCardService.deletePaymentCard(cardId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{card_id}")
    public ResponseEntity<PaymentCardDTO> getCardById(
            @PathVariable("card_id") UUID id
    ) {
        PaymentCardDTO card = paymentCardService.getPaymentCardById(id);
        return ResponseEntity.ok(card);
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardDTO>> getAllCards(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Page<PaymentCardDTO> paymentCardDtos = paymentCardService.getAllPaymentCards(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(paymentCardDtos);
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<List<PaymentCardDTO>> getAllCardsByUserId(
            @PathVariable("user_id") UUID userId
    ) {
        List<PaymentCardDTO> paymentCardDtos = paymentCardService
                .getAllPaymentCardByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentCardDtos);
    }

    @PostMapping
    public ResponseEntity<?> addCard(
            UUID userId,
            @RequestBody @Valid PaymentCardDTO paymentCardDto
    ) {
        PaymentCardDTO createdCard = paymentCardService
                .createCard(userId, paymentCardDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @PatchMapping("/{card_id}")
    public ResponseEntity<?> updateCard(@PathVariable("card_id") UUID cardId,
                                              @RequestBody @Valid PaymentCardDTO cardDTO
    ) {
        PaymentCardDTO updateCard = paymentCardService
                .updateCard(cardId, cardDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updateCard);
    }

    @PatchMapping("/activate/{card_id}")
    public ResponseEntity<PaymentCardDTO> activateCard(@PathVariable("card_id") UUID cardId) {
        PaymentCardDTO activatedCardDto = paymentCardService.activatePaymentCard(cardId);

        return ResponseEntity.status(HttpStatus.OK).body(activatedCardDto);
    }

    @PatchMapping("/deactivate/{card_id}")
    public ResponseEntity<PaymentCardDTO> deactivateCard(@PathVariable("card_id") UUID cardId) {
        PaymentCardDTO deactivatedCardDto = paymentCardService.deactivatePaymentCard(cardId);

        return ResponseEntity.status(HttpStatus.OK).body(deactivatedCardDto);
    }
}
