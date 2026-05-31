package com.example.user_service_inno.service;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    @CachePut(value = "users", key = "#result.id")
    public UserDTO createUser(UserDTO userDTO){
        User saved = userRepository.save(userMapper.toEntity(userDTO));
        return userMapper.toDto(saved);
    }

    @Cacheable(value = "users", key = "#id")
    public UserDTO getUserById(UUID id){
        return userRepository.findById(id)
            .map(userMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserDTO updateUser(UUID id, UserDTO userDTO){
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

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

    
    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserDTO activateUser(UUID id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setIsActive(true);
        return userMapper.toDto(user);
    }
    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserDTO deactivateUser(UUID id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setIsActive(false);
        return userMapper.toDto(user);
    }
    
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUserById(UUID id){
        if (userRepository.existsById(id)){
            userRepository.deleteById(id);
        }
        else{
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }
}
