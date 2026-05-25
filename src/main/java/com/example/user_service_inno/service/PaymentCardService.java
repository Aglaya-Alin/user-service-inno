package com.example.user_service_inno.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_service_inno.api.dto.PaymentCardDTO;
import com.example.user_service_inno.api.mapper.PaymentCardMapper;
import com.example.user_service_inno.entity.PaymentCard;
import com.example.user_service_inno.entity.User;
import com.example.user_service_inno.repository.PaymentCardRepository;
import com.example.user_service_inno.repository.UserRepository;
import com.example.user_service_inno.service.exceptions.CardLimitExceededException;
import com.example.user_service_inno.service.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;
    
    @Transactional
    public PaymentCardDTO createCard(UUID userId, PaymentCardDTO paymentCardDTO){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        int cardCount = paymentCardRepository.countByUserId(userId);
        if (cardCount >= 5) {
            throw new CardLimitExceededException("User with id " + userId + " already has the maximum number of cards (5)");
        }

        PaymentCard paymentCard = paymentCardMapper.toEntity(paymentCardDTO);
        
        paymentCard.setUser(user);

        PaymentCard savedCard = paymentCardRepository.save(paymentCard);

        return paymentCardMapper.toDto(savedCard);
    }

    public PaymentCardDTO getPaymentCardById(UUID id){
        return paymentCardRepository.findById(id)
            .map(paymentCardMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
    }

    @Transactional
    public PaymentCardDTO updateCard(UUID id, PaymentCardDTO paymentCardDTO){
        PaymentCard existingCard = paymentCardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));

        paymentCardMapper.updateEntityFromDto(paymentCardDTO, existingCard);

        PaymentCard updatedCard = paymentCardRepository.save(existingCard);
        return paymentCardMapper.toDto(updatedCard);     
    }

    public void deletePaymentCard(UUID id){
        PaymentCard card = paymentCardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        User user = card.getUser();
        if (user != null && user.getCards() != null) {
            user.getCards().remove(card);
        }
        paymentCardRepository.delete(card);
        
    }

    @Transactional
    public Page<PaymentCardDTO> getAllPaymentCards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<PaymentCard> paymentCardPage = paymentCardRepository.findAll(pageable);

        return paymentCardPage.map(paymentCardMapper::toDto);
    }

    public List<PaymentCardDTO> getAllPaymentCardByUserId(UUID userId){
        
        List<PaymentCard> card = paymentCardRepository.getAllPaymentCardByUserId(userId);
        return card.stream()
            .map(paymentCardMapper::toDto)
            .toList();
    }

    @Transactional
    public PaymentCardDTO activatePaymentCard(UUID id){
        PaymentCard card = paymentCardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        card.setIsActive(true);
        paymentCardRepository.save(card);
        return paymentCardMapper.toDto(card);
    }
    
    @Transactional
    public PaymentCardDTO deactivatePaymentCard(UUID id){
        PaymentCard card = paymentCardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        card.setIsActive(false);
        paymentCardRepository.save(card);
        return paymentCardMapper.toDto(card);
    }
    

}
