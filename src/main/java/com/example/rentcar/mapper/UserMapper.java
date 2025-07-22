package com.example.rentcar.mapper;

import com.example.rentcar.dto.SaveUserRequest;
import com.example.rentcar.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SaveUserRequest saveUserRequest);
}