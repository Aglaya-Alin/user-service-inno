package com.example.user_service_inno.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_service_inno.api.dto.UserDTO;
import com.example.user_service_inno.api.mapper.UserMapper;
import com.example.user_service_inno.entity.User;
import com.example.user_service_inno.repository.UserRepository;
import com.example.user_service_inno.specification.UserSpecifications;

import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public User createUser(UserDTO userDTO){
        try {
            User saved = userRepository.save(userMapper.toEntity(userDTO));
            return saved;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Optional<User> getUserById(UUID userId){
        return userRepository.findById(userId);
    }

    @Transactional
    public UserDTO updateUser(UUID userId, UserDTO userDTO){
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        userMapper.updateEntityFromDto(userDTO, existingUser);

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public Page<UserDTO> getAllUsersByNameOrSurname(String name, String surname, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<User> spec = Specification
                .where(UserSpecifications.hasFirstName(name))
                .and(UserSpecifications.hasSurname(surname));

        Page<User> userPage = userRepository.findAll(spec, pageable);

        return userPage.map(userMapper::toDto);
    }

    public void activateUser(UUID userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setIsActive(true);
        userRepository.save(user);
    }
    
    @Transactional
    public void deleteUserById(UUID id){
        if (userRepository.existsById(id)){
            userRepository.deleteById(id);
        }
    }

}
