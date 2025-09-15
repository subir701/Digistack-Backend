package com.digistackBackend.service;

import com.digistackBackend.dto.LoginRequestDTO;
import com.digistackBackend.dto.UserRequestDTO;
import com.digistackBackend.dto.UserResponseDTO;
import com.digistackBackend.exception.InvalidCredentialsException;
import com.digistackBackend.exception.UserAlreadyExistsException;
import com.digistackBackend.exception.UserNotFoundException;
import com.digistackBackend.mapper.UserMapper;
import com.digistackBackend.model.User;
import com.digistackBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO requestDTO) throws UserAlreadyExistsException {
        if (userRepo.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists: " + requestDTO.getEmail());
        }
        if (userRepo.findByMobileNumber(requestDTO.getMobileNumber()).isPresent()) {
            throw new UserAlreadyExistsException("Mobile already exists: " + requestDTO.getMobileNumber());
        }

        User entity = UserMapper.toEntity(requestDTO);
        return UserMapper.toDto(userRepo.save(entity));
    }

    @Override
    public void userLogin(LoginRequestDTO requestDTO) throws InvalidCredentialsException {
        User existingUser = userRepo.findByEmail(requestDTO.getEmail()).orElseThrow(() -> new InvalidCredentialsException("User not found with username: " + requestDTO.getEmail()));
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDTO.getEmail(), requestDTO.getPassword()));
        if(authentication.isAuthenticated()){
            String accessToken = jwtService.generateToken(existingUser.getEmail(),existingUser.getRole());
            System.out.println("Sucessfully Login "+accessToken);
        }else{
            throw new UserNotFoundException("Invalid username or password");
        }
    }

    @Override
    @Transactional
    public UserResponseDTO updateEmailUser(UUID userId, String updatedemail) throws UserNotFoundException {
        User entity = userRepo.findById(userId).orElseThrow(()->new UserNotFoundException("User not found id "+userId));
        entity.setEmail(updatedemail);
        return UserMapper.toDto(userRepo.save(entity));

    }

    @Override
    public UserResponseDTO getUser(String email) throws UserNotFoundException {
        return UserMapper.toDto(userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("User not found with email "+email)));
    }
}
