package com.example.user_service_inno.service.integration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.user_service_inno.api.dto.PaymentCardDTO;
import com.example.user_service_inno.entity.PaymentCard;
import com.example.user_service_inno.entity.User;
import com.example.user_service_inno.repository.PaymentCardRepository;
import com.example.user_service_inno.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;


@ActiveProfiles("test")
public class PaymentCardIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); 

    @BeforeEach
    public void cleanDatabase() {
        paymentCardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreatePaymentCardAndSaveToDatabase() throws Exception {
        
        User user = new User();
        user.setName("Ivan");
        user.setSurname("Ivanov");
        user.setEmail("ivan@test.com");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setIsActive(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user = userRepository.save(user);

        
        PaymentCardDTO cardDto = new PaymentCardDTO(
                null,
                user.getId(),
                "1234567812345678",
                "IVAN IVANOV",
                YearMonth.of(2030, 1),
                true,
                Instant.now(),
                Instant.now()
            );
        mockMvc.perform(post("/cards")
                        .param("userId", user.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("1234567812345678"))
                .andExpect(jsonPath("$.holder").value("IVAN IVANOV"));

        assertThat(paymentCardRepository.findAll()).hasSize(1);
        PaymentCard savedCard = paymentCardRepository.findAll().get(0);
        assertThat(savedCard.getNumber()).isEqualTo("1234567812345678");
        assertThat(savedCard.getHolder()).isEqualTo("IVAN IVANOV");
    }

    @Test
    void shouldReturnCardById() throws Exception {

        User user = new User();
        user.setName("John");
        user.setSurname("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setEmail("john.doe@example.com");
        user.setIsActive(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        user = userRepository.save(user); 


        PaymentCard card = new PaymentCard();
        card.setNumber("9876543298765432");
        card.setHolder("TEST HOLDER");
        card.setExpirationDate(YearMonth.of(2030,1));
        card.setCreatedAt(Instant.now());
        card.setUpdatedAt(Instant.now());

        card.setUser(user); 

        card = paymentCardRepository.save(card);

        mockMvc.perform(get("/cards/{cardId}", card.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("9876543298765432"))
                .andExpect(jsonPath("$.holder").value("TEST HOLDER"));
    }



   @Test
    void shouldReturnAllCardsByUserId() throws Exception {
        User user = new User();
        user.setName("aglaja");
        user.setSurname("a");
        user.setEmail("Aglaja.test@gmail.com");
        user.setBirthDate(LocalDate.of(2000, 1, 10));
        user.setIsActive(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user = userRepository.save(user);
        user = userRepository.save(user);

        PaymentCard card = new PaymentCard();
        card.setNumber("5555666677778888");
        card.setHolder("USER CARDS HOLDER");
        card.setExpirationDate(YearMonth.of(2030,1));
        card.setCreatedAt(Instant.now());
        card.setUpdatedAt(Instant.now());
        card.setUser(user);
        paymentCardRepository.save(card);

        mockMvc.perform(get("/cards/user/{userId}", user.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldDeletePaymentCard() throws Exception {
        User user = new User();
        user.setName("aglaja");
        user.setSurname("a");
        user.setEmail("Aglaja.test@gmail.com");
        user.setBirthDate(LocalDate.of(2000, 1, 10));
        user.setIsActive(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user = userRepository.save(user);

        PaymentCard card = new PaymentCard();
        card.setNumber("4444555566667777");
        card.setHolder("TO BE DELETED");
        card.setExpirationDate(YearMonth.of(2030,1));
        card.setCreatedAt(Instant.now());
        card.setUpdatedAt(Instant.now());
        card.setUser(user);
        card = paymentCardRepository.save(card);

        mockMvc.perform(delete("/cards/{cardId}", card.getId()))
                .andExpect(status().isNoContent());

        entityManager.clear();
        assertThat(paymentCardRepository.findById(card.getId())).isEmpty();
    }


}
