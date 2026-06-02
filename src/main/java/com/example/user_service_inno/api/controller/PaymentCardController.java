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
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/cards")
@RestController
public class PaymentCardController {
    private final PaymentCardService paymentCardService;

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID cardId) {
        paymentCardService.deletePaymentCard(cardId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<PaymentCardDTO> getCardById(
            @PathVariable UUID cardId
    ) {
        PaymentCardDTO card = paymentCardService.getPaymentCardById(cardId);
        return ResponseEntity.ok(card);
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardDTO>> getAllCards(
        @RequestParam(required = false) String holder,
        @RequestParam(required = false) Boolean isActive,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Page<PaymentCardDTO> paymentCardDtos = paymentCardService.getAllPaymentCards(holder, isActive, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(paymentCardDtos);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentCardDTO>> getAllCardsByUserId(
            @PathVariable UUID userId
    ) {
        List<PaymentCardDTO> paymentCardDtos = paymentCardService.getAllPaymentCardByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(paymentCardDtos);
    }


    @PostMapping
    public ResponseEntity<PaymentCardDTO> addCard(
            @RequestParam UUID userId,
            @RequestBody @Valid PaymentCardDTO paymentCardDto
    ) {
        PaymentCardDTO createdCard = paymentCardService
                .createCard(userId, paymentCardDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @PatchMapping("/{cardId}")
    public ResponseEntity<PaymentCardDTO> updateCard(@PathVariable UUID cardId,
                                              @RequestBody @Valid PaymentCardDTO cardDTO
    ) {
        PaymentCardDTO updateCard = paymentCardService
                .updateCard(cardId, cardDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updateCard);
    }

    @PatchMapping("/activate/{cardId}")
    public ResponseEntity<PaymentCardDTO> activateCard(@PathVariable UUID cardId) {
        PaymentCardDTO activatedCardDto = paymentCardService.activatePaymentCard(cardId);

        return ResponseEntity.status(HttpStatus.OK).body(activatedCardDto);
    }

    @PatchMapping("/deactivate/{cardId}")
    public ResponseEntity<PaymentCardDTO> deactivateCard(@PathVariable UUID cardId) {
        PaymentCardDTO deactivatedCardDto = paymentCardService.deactivatePaymentCard(cardId);

        return ResponseEntity.status(HttpStatus.OK).body(deactivatedCardDto);
    }
}
