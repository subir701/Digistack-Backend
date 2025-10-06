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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO requestDTO) throws UserAlreadyExistsException {

        log.info("Attempting to register user with email: {} and mobile: {}", requestDTO.getEmail(), requestDTO.getMobileNumber());

        if (userRepo.findByEmail(requestDTO.getEmail()).isPresent()) {
            log.warn("Registration failed: Email already exists: {}",requestDTO.getEmail());
            throw new UserAlreadyExistsException("Email already exists: " + requestDTO.getEmail());
        }
        if (userRepo.findByMobileNumber(requestDTO.getMobileNumber()).isPresent()) {
            log.warn("Registration failed: Mobile nubmer already exist: {}",requestDTO.getMobileNumber());
            throw new UserAlreadyExistsException("Mobile already exists: " + requestDTO.getMobileNumber());
        }

        User entity = UserMapper.toEntity(requestDTO);
        User savedUser = userRepo.save(entity);
        log.info("User successfully registered with id: {}",savedUser);
        return UserMapper.toDto(savedUser);
    }

    @Override
    public void userLogin(LoginRequestDTO requestDTO) throws InvalidCredentialsException {
        log.info("Attmepting login for user: {}",requestDTO.getEmail());
        User existingUser = userRepo.findByEmail(requestDTO.getEmail()).orElseThrow(() -> {
            log.warn("Login failed: User not found with email: {}",requestDTO.getEmail());
            return new InvalidCredentialsException("User not found with username: " + requestDTO.getEmail());
        });
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDTO.getEmail(), requestDTO.getPassword()));
        if(authentication.isAuthenticated()){
            String accessToken = jwtService.generateToken(existingUser.getEmail(),existingUser.getRole());
            log.info("User login scusseful for email: {}", existingUser.getEmail());
            log.debug("JWT issued: {}", accessToken);
            System.out.println("Sucessfully Login "+accessToken);
        }else{
            log.warn("Login failed: Invalid credentials for {}",requestDTO.getEmail());
            throw new UserNotFoundException("Invalid username or password");
        }
    }

    @Override
    @Transactional
    public UserResponseDTO updateEmailUser(UUID userId, String updatedemail) throws UserNotFoundException {
        log.info("Updating email for userId: {} to: {}", userId, updatedemail);
        User entity = userRepo.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Update failed: User not found with id: {}", userId);
                    return new UserNotFoundException("User not found id "+userId);
                });
        entity.setEmail(updatedemail);
        User updatedUser = userRepo.save(entity);
        log.info("User email successfully updated for userId: {}", userId);
        return UserMapper.toDto(updatedUser);

    }

    @Override
    public UserResponseDTO getUser(String email) throws UserNotFoundException {
        log.info("Fetching user with email: {}", email);
        UserResponseDTO userResponseDTO = UserMapper.toDto(
                userRepo.findByEmail(email)
                        .orElseThrow(() -> {
                            log.warn("GetUser failed: No user found with email: {}", email);
                            return new UserNotFoundException("User not found with email "+email);
                        })
        );
        log.info("User fetched successfully for email: {}", email);
        return userResponseDTO;
    }
}
