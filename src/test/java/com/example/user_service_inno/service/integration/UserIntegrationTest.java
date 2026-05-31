package com.example.user_service_inno.service.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.ActiveProfiles;
import com.example.user_service_inno.api.dto.UserDTO;
import com.example.user_service_inno.entity.User;
import com.example.user_service_inno.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;



@ActiveProfiles("test")
public class UserIntegrationTest extends BaseIntegrationTest{
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); 
    @BeforeEach
    public void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUserAndSaveToDatabase() throws Exception {

        UserDTO userDto = new UserDTO(
                null,
                "Aglaja",
                "A",
                LocalDate.of(2000, 1, 10),
                "Aglaja.test@gmail.com",
                true,
                Instant.now(),
                Instant.now(),
                Collections.emptyList()
        );

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Aglaja"));


        assertThat(userRepository.findAll()).hasSize(1);
        User savedUser = userRepository.findAll().get(0);
        assertThat(savedUser.getName()).isEqualTo("Aglaja");
        assertThat(savedUser.getEmail()).isEqualTo("Aglaja.test@gmail.com");
    }

    @Test
    void shouldReturnUserById() throws Exception {
        User user = new User();
        user.setName("Aglaja");
        user.setSurname("A");
        user.setEmail("Aglaja.test@gmail.com");
        user.setBirthDate(LocalDate.of(2000, 1, 10));
        user.setIsActive(true);
        user = userRepository.save(user);

        mockMvc.perform(get("/users/{user_id}", user.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aglaja"))
                .andExpect(jsonPath("$.email").value("Aglaja.test@gmail.com"));
    }

    
    @Test
    void shouldReturnAllUsers() throws Exception {
        User user = new User();
        user.setName("aglaja");
        user.setSurname("a");
        user.setEmail("Aglaja.test@gmail.com");
        user.setBirthDate(LocalDate.of(2000, 1, 10));
        user.setIsActive(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user = userRepository.save(user);

        mockMvc.perform(get("/users")
                        .param("name", "aglaja")
                        .param("surname", "a")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    
    @Test
    void shouldUpdateUser() throws Exception {
        
        User user = new User();
        user.setName("Aglaja");
        user.setSurname("A");
        user.setEmail("Aglaja.test@gmail.com");
        user.setBirthDate(LocalDate.of(2000, 1, 10));
        user.setIsActive(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user = userRepository.save(user);

        UserDTO updateDto = new UserDTO(
                user.getId(),
                "Maria",
                "Il",
                LocalDate.of(2000, 5, 10),
                "Maria.test@mail.com",
                true,
                Instant.now(),
                Instant.now(),
                Collections.emptyList()
        );

        mockMvc.perform(patch("/users/{user_id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                        .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria"));

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Maria");
    }
    

    @Test
    void shouldDeleteUser() throws Exception {
        User user = new User();
        user.setName("Aglaja");
        user.setSurname("A");
        user.setEmail("Aglaja.test@gmail.com");
        user.setBirthDate(LocalDate.of(2000, 5, 10));
        user.setIsActive(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user = userRepository.save(user);

        mockMvc.perform(delete("/users/{user_id}", user.getId()))
                .andExpect(status().is(204));

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    
    @Test
    void shouldActivateUser() throws Exception {
        User user = new User();
        user.setName("Aglaja");
        user.setSurname("A");
        user.setEmail("Aglaja.test@gmail.com");
        user.setBirthDate(LocalDate.of(2000, 5, 10));
        user.setIsActive(false);
        user = userRepository.save(user);

        mockMvc.perform(patch("/users/activate/{user_id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(true)); 

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getIsActive()).isTrue();
    }

    @Test
    void shouldDeactivateUser() throws Exception {
        User user = new User();
        user.setName("Aglaja");
        user.setSurname("A");
        user.setEmail("Aglaja.test@gmail.com");
        user.setBirthDate(LocalDate.of(2000, 5, 10));
        user.setIsActive(true);
        user = userRepository.save(user);

        mockMvc.perform(patch("/users/deactivate/{user_id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getIsActive()).isFalse();
    }
}
