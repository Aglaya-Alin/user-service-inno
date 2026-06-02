package com.example.user_service_inno.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.user_service_inno.api.dto.PaymentCardDTO;
import com.example.user_service_inno.api.dto.UserDTO;
import com.example.user_service_inno.api.mapper.PaymentCardMapper;
import com.example.user_service_inno.entity.PaymentCard;
import com.example.user_service_inno.entity.User;
import com.example.user_service_inno.repository.PaymentCardRepository;
import com.example.user_service_inno.repository.UserRepository;
import com.example.user_service_inno.service.exceptions.CardLimitExceededException;
import com.example.user_service_inno.service.exceptions.ResourceNotFoundException;
import com.example.user_service_inno.api.mapper.UserMapper;


@ExtendWith(MockitoExtension.class)
public class PaymentCardServiceTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private PaymentCardService paymentCardService;

    private UUID userId;
    private UUID cardId;
    private User userMockEntity;
    private PaymentCard cardMockEntity;
    private PaymentCardDTO cardDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cardId = UUID.randomUUID();

        userMockEntity = new User();
        userMockEntity.setId(userId);
        userMockEntity.setCards(new ArrayList<>());
        cardMockEntity = new PaymentCard();
        cardMockEntity.setId(cardId);
        cardMockEntity.setUser(userMockEntity);
        cardMockEntity.setIsActive(true);

        cardDto = new PaymentCardDTO(
                cardId,
                userId,
                "1234567812345678",
                "JOHN DOE",
                YearMonth.of(2030,1),
                true,
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void createCard_WhenUserExistsAndLimitNotExceeded_ShouldReturnCreatedCardDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userMockEntity));
        when(paymentCardRepository.countByUserId(userId)).thenReturn(4);
        when(paymentCardMapper.toEntity(cardDto)).thenReturn(cardMockEntity);
        when(paymentCardRepository.save(cardMockEntity)).thenReturn(cardMockEntity);
        when(paymentCardMapper.toDto(cardMockEntity)).thenReturn(cardDto);

        PaymentCardDTO result = paymentCardService.createCard(userId, cardDto);

        assertNotNull(result);
        assertEquals(cardId, result.id());
        verify(paymentCardRepository, times(1)).save(cardMockEntity);
    }

    @Test
    void createCard_WhenUserDoesNotExist_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentCardService.createCard(userId, cardDto));
        verify(paymentCardRepository, never()).save(any());
    }

    @Test
    void createCard_WhenCardLimitExceeded_ShouldThrowCardLimitExceededException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userMockEntity));
        when(paymentCardRepository.countByUserId(userId)).thenReturn(5); // Ужо ёсць 5 карт

        assertThrows(CardLimitExceededException.class, () -> paymentCardService.createCard(userId, cardDto));
        verify(paymentCardRepository, never()).save(any());
    }


    @Test
    void getPaymentCardById_WhenCardExists_ShouldReturnCardDto() {
        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(cardMockEntity));
        when(paymentCardMapper.toDto(cardMockEntity)).thenReturn(cardDto);

        PaymentCardDTO result = paymentCardService.getPaymentCardById(cardId);

        assertNotNull(result);
        assertEquals(cardId, result.id());
    }

    @Test
    void getPaymentCardById_WhenCardDoesNotExist_ShouldThrowResourceNotFoundException() {
        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentCardService.getPaymentCardById(cardId));
    }

    @Test
    void updateCard_WhenCardExists_ShouldReturnUpdatedCardDto() {
        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(cardMockEntity));
        doNothing().when(paymentCardMapper).updateEntityFromDto(cardDto, cardMockEntity);
        when(paymentCardRepository.save(cardMockEntity)).thenReturn(cardMockEntity);
        when(paymentCardMapper.toDto(cardMockEntity)).thenReturn(cardDto);

        PaymentCardDTO result = paymentCardService.updateCard(cardId, cardDto);

        assertNotNull(result);
        verify(paymentCardMapper, times(1)).updateEntityFromDto(cardDto, cardMockEntity);
        verify(paymentCardRepository, times(1)).save(cardMockEntity);
    }

    @Test
    void updateCard_WhenCardDoesNotExist_ShouldThrowResourceNotFoundException() {
        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentCardService.updateCard(cardId, cardDto));
    }

    @Test
    void deletePaymentCard_WhenCardHasUser_ShouldRemoveCardFromUserAndReturnUserDto() {
        // Добавляем карту в список пользователя перед удалением для проверки ветки IF
        userMockEntity.getCards().add(cardMockEntity);
        
        UserDTO expectedUserDto = new UserDTO(
                userId,
                "John",
                "Doe",
                LocalDate.now().minusYears(25),
                "john.doe@example.com",
                true,
                Instant.now(),
                Instant.now(),
                Collections.emptyList()
        );

        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(cardMockEntity));
        when(userMapper.toDto(userMockEntity)).thenReturn(expectedUserDto);

        UserDTO result = paymentCardService.deletePaymentCard(cardId);

        assertNotNull(result);
        assertFalse(userMockEntity.getCards().contains(cardMockEntity));
        assertEquals(userId, result.id());
        verify(userMapper, times(1)).toDto(userMockEntity);
    }

    @Test
    void deletePaymentCard_WhenCardHasNoUser_ShouldReturnNull() {
        cardMockEntity.setUser(null);

        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(cardMockEntity));
        when(userMapper.toDto(null)).thenReturn(null);

        UserDTO result = paymentCardService.deletePaymentCard(cardId);

        assertNull(result);
    }

    @Test
    void deletePaymentCard_WhenCardDoesNotExist_ShouldThrowResourceNotFoundException() {
        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentCardService.deletePaymentCard(cardId));
        verify(paymentCardRepository, never()).delete(any(PaymentCard.class));
    }


    @SuppressWarnings("unchecked")
    @Test
    void getAllPaymentCards_ShouldReturnPageOfCardDtos() {
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentCard> cardPage = new PageImpl<>(Collections.singletonList(cardMockEntity));

        when(paymentCardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cardPage);
        when(paymentCardMapper.toDto(cardMockEntity)).thenReturn(cardDto);

        Page<PaymentCardDTO> result = paymentCardService.getAllPaymentCards("JOHN DOE", true, page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(cardId, result.getContent().get(0).id());
    }



    @Test
    void getAllPaymentCardByUserId_ShouldReturnListOfCardDtos() {
        List<PaymentCard> mockList = Collections.singletonList(cardMockEntity);
        when(paymentCardRepository.getAllPaymentCardByUserId(userId)).thenReturn(mockList);
        when(paymentCardMapper.toDto(cardMockEntity)).thenReturn(cardDto);

        List<PaymentCardDTO> result = paymentCardService.getAllPaymentCardByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cardId, result.get(0).id());
    }


    @Test
    void activatePaymentCard_WhenCardExists_ShouldSetActiveTrueAndReturnDto() {
        cardMockEntity.setIsActive(false);
        
        PaymentCardDTO activeCardDto = new PaymentCardDTO(
                cardDto.id(), cardDto.userId(), cardDto.number(), cardDto.holder(),
                cardDto.expirationDate(), true, cardDto.createdAt(), cardDto.updatedAt()
        );

        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(cardMockEntity));
        when(paymentCardMapper.toDto(cardMockEntity)).thenReturn(activeCardDto);

        PaymentCardDTO result = paymentCardService.activatePaymentCard(cardId);

        assertNotNull(result);
        assertTrue(cardMockEntity.getIsActive());
        assertTrue(result.isActive());
    }

    @Test
    void deactivatePaymentCard_WhenCardExists_ShouldSetActiveFalseAndReturnDto() {
        cardMockEntity.setIsActive(true);

        PaymentCardDTO inactiveCardDto = new PaymentCardDTO(
                cardDto.id(), cardDto.userId(), cardDto.number(), cardDto.holder(),
                cardDto.expirationDate(), false, cardDto.createdAt(), cardDto.updatedAt()
        );

        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(cardMockEntity));
        when(paymentCardMapper.toDto(cardMockEntity)).thenReturn(inactiveCardDto);

        PaymentCardDTO result = paymentCardService.deactivatePaymentCard(cardId);

        assertNotNull(result);
        assertFalse(cardMockEntity.getIsActive());
        assertFalse(result.isActive());
    }


    @Test
    void deactivatePaymentCard_WhenCardDoesNotExist_ShouldThrowResourceNotFoundException() {
        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentCardService.deactivatePaymentCard(cardId));
    }

    @Test
    void activatePaymentCard_WhenCardDoesNotExist_ShouldThrowResourceNotFoundException() {
        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentCardService.activatePaymentCard(cardId));
    }
    
}
