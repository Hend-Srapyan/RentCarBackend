package com.example.rentcar.service;

import com.example.rentcar.dto.SaveUserRequest;
import com.example.rentcar.dto.UserAuthRequest;
import com.example.rentcar.dto.UserAuthResponse;
import com.example.rentcar.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {

    void save(SaveUserRequest user);

    Optional<User> findByUsername(String username);

    ResponseEntity<UserAuthResponse> login(UserAuthRequest userAuthRequest);

    ResponseEntity<?> register(SaveUserRequest saveUserRequest);
}