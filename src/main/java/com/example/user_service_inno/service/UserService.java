package com.example.user_service_inno.service;

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
import com.example.user_service_inno.service.exceptions.ResourceNotFoundException;
import com.example.user_service_inno.specification.UserSpecifications;

import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDTO createUser(UserDTO userDTO){
        User saved = userRepository.save(userMapper.toEntity(userDTO));
        return userMapper.toDto(saved);
    }

    public UserDTO getUserById(UUID userId){
        return userRepository.findById(userId)
            .map(userMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Transactional
    public UserDTO updateUser(UUID userId, UserDTO userDTO){
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

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

    public UserDTO activateUser(UUID userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setIsActive(true);
        userRepository.save(user);
        return userMapper.toDto(user);
    }
    public UserDTO deactivateUser(UUID userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setIsActive(false);
        userRepository.save(user);
        return userMapper.toDto(user);
    }
    
    @Transactional
    public void deleteUserById(UUID id){
        if (userRepository.existsById(id)){
            userRepository.deleteById(id);
        }
        else{
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }

}
