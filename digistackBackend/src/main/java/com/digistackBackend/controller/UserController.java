package com.digistackBackend.controller;

import com.digistackBackend.dto.LoginRequestDTO;
import com.digistackBackend.dto.UserRequestDTO;
import com.digistackBackend.dto.UserResponseDTO;
import com.digistackBackend.exception.InvalidCredentialsException;
import com.digistackBackend.exception.UserAlreadyExistsException;
import com.digistackBackend.exception.UserNotFoundException;
import com.digistackBackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRequestDTO dto) throws UserAlreadyExistsException {
        dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        UserResponseDTO createdUser = userService.registerUser(dto);
        return new ResponseEntity<>(createdUser, HttpStatus.ACCEPTED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO dto) throws InvalidCredentialsException {
        userService.userLogin(dto);
        return ResponseEntity.ok("Login successful");
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<UserResponseDTO> updateEmail(@PathVariable UUID id, @RequestParam String newEmail) throws UserNotFoundException {
        return ResponseEntity.ok(userService.updateEmailUser(id, newEmail));
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String email) throws UserNotFoundException {
        return ResponseEntity.ok(userService.getUser(email));
    }
}