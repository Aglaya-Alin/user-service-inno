package com.example.user_service_inno.api.controller;

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

import com.example.user_service_inno.api.dto.UserDTO;
import com.example.user_service_inno.service.UserService;

import jakarta.validation.Valid;

@RequestMapping("/users")
@RestController
public class UserController {
    @Autowired private UserService userService;


    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<UserDTO> pageUser = userService.getAllUsersByNameOrSurname(name, surname, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(pageUser);
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody @Valid UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PatchMapping("/{user_id}")
    public ResponseEntity<?> updateUser(@PathVariable("user_id") UUID userId,
                              @RequestBody @Valid UserDTO userDto
    ) {
        UserDTO updatedUserDto = userService.updateUser(userId, userDto);

        return ResponseEntity.status(HttpStatus.OK).body(updatedUserDto);
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/activate/{user_id}")
    public ResponseEntity<?> activateUser(@PathVariable("id") UUID userId) {
        UserDTO activatedUserDto = userService.activateUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(activatedUserDto);
    }

    @PatchMapping("/deactivate/{user_id}")
    public ResponseEntity<?> deactivateUser(@PathVariable("id") UUID userId) {
        UserDTO deactivatedUserDto = userService.deactivateUser(userId);

        return ResponseEntity.status(HttpStatus.OK).body(deactivatedUserDto);
    }
}
