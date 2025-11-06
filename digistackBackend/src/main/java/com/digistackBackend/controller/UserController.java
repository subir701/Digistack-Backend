package com.digistackBackend.controller;

import com.digistackBackend.dto.LoginRequestDTO;
import com.digistackBackend.dto.UserRequestDTO;
import com.digistackBackend.dto.UserResponseDTO;
import com.digistackBackend.exception.InvalidCredentialsException;
import com.digistackBackend.exception.UserAlreadyExistsException;
import com.digistackBackend.exception.UserNotFoundException;
import com.digistackBackend.service.JwtService;
import com.digistackBackend.service.UserService;
import com.digistackBackend.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Valid UserRequestDTO dto) throws UserAlreadyExistsException {
        dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        UserResponseDTO createdUser = userService.registerUser(dto);
        return new ResponseEntity<>(createdUser, HttpStatus.ACCEPTED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO dto, HttpServletResponse httpServletResponse) throws InvalidCredentialsException {
       HashMap<String,String> tokens = userService.userLogin(dto);

        CookieUtil.addCookie(httpServletResponse,"accessToken",tokens.get("accessToken"),60000);
        CookieUtil.addCookie(httpServletResponse,"refreshToken",tokens.get("refreshToken"),60000);

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

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser(@CookieValue(value = "accessToken", required = false)String token){
        if(token == null || token.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No Token found");
        }

        if(!jwtService.validateToken(token))return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");

        String email = jwtService.extractUsername(token);
        UserResponseDTO userResponseDTO = userService.getUser(email);

        return ResponseEntity.ok(userResponseDTO);
    }
}