package com.example.user_service_inno.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.example.user_service_inno.api.dto.UserDTO;
import com.example.user_service_inno.api.mapper.UserMapper;
import com.example.user_service_inno.entity.User;
import com.example.user_service_inno.repository.UserRepository;
import com.example.user_service_inno.service.exceptions.ResourceNotFoundException;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User user;
    private UserDTO userDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setIsActive(true);

        
        userDto = new UserDTO(
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
    }

    @Test
    void createUser_ShouldReturnCreatedUserDto() {
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDTO result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(userId, result.id());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUserDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.id());
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUserDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateEntityFromDto(userDto, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDTO result = userService.updateUser(userId, userDto);

        assertNotNull(result);
        verify(userMapper, times(1)).updateEntityFromDto(userDto, user);
        verify(userRepository, times(1)).save(user);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    void getAllUsersByNameOrSurname_ShouldReturnPageOfUserDtos() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toDto(user)).thenReturn(userDto);

        Page<UserDTO> result = userService.getAllUsersByNameOrSurname("John", "Doe", page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(userId, result.getContent().get(0).id());
    }

    @Test
    void activateUser_WhenUserExists_ShouldSetActiveTrueAndReturnDto() {
        user.setIsActive(false); 
        UserDTO activeUserDto = new UserDTO(
                userDto.id(), userDto.name(), userDto.surname(), userDto.birthDate(),
                userDto.email(), true, userDto.createdAt(), userDto.updatedAt(), userDto.cards()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(activeUserDto);

        UserDTO result = userService.activateUser(userId);

        assertNotNull(result);
        assertTrue(user.getIsActive());
        assertTrue(result.isActive());
    }

    @Test
    void deactivateUser_WhenUserExists_ShouldSetActiveFalseAndReturnDto() {
        user.setIsActive(true);
        
        UserDTO inactiveUserDto = new UserDTO(
                userDto.id(), userDto.name(), userDto.surname(), userDto.birthDate(),
                userDto.email(), false, userDto.createdAt(), userDto.updatedAt(), userDto.cards()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(inactiveUserDto);

        UserDTO result = userService.deactivateUser(userId);

        assertNotNull(result);
        assertFalse(user.getIsActive());
        assertFalse(result.isActive());
    }

    @Test
    void deleteUserById_WhenUserExists_ShouldDeleteUser() {
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.deleteUserById(userId));
        verify(userRepository, times(1)).deleteById(userId);
    }
}
