package com.digistackBackend.service;

import com.digistackBackend.dto.LoginRequestDTO;
import com.digistackBackend.dto.UserRequestDTO;
import com.digistackBackend.dto.UserResponseDTO;
import com.digistackBackend.exception.InvalidCredentialsException;
import com.digistackBackend.exception.UserAlreadyExistsException;
import com.digistackBackend.exception.UserNotFoundException;

import java.util.UUID;

public interface UserService {
    UserResponseDTO registerUser(UserRequestDTO requestDTO)throws UserAlreadyExistsException;

    void userLogin(LoginRequestDTO requestDTO)throws InvalidCredentialsException;

    UserResponseDTO updateEmailUser(UUID userId, String updatedemail)throws UserNotFoundException;

    UserResponseDTO getUser(String email)throws UserNotFoundException;
}
