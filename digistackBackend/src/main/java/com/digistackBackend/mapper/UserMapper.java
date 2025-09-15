package com.digistackBackend.mapper;

import com.digistackBackend.dto.UserRequestDTO;
import com.digistackBackend.dto.UserResponseDTO;
import com.digistackBackend.model.User;
public final class UserMapper {

    private UserMapper(){}
    public static User toEntity(UserRequestDTO requestDTO){

        if(requestDTO == null)return null;

        User entity = new User();

        entity.setName(requestDTO.getName());
        entity.setEmail(requestDTO.getEmail());
        entity.setMobileNumber(requestDTO.getMobileNumber());
        entity.setRole(requestDTO.getRole());
        entity.setPassword(requestDTO.getPassword());

        return entity;
    }

    public static UserResponseDTO toDto(User entity){
        if(entity == null)return null;

        return UserResponseDTO.builder()
                .email(entity.getEmail())
                .name(entity.getName())
                .mobileNumber(entity.getMobileNumber())
                .build();

    }
}
