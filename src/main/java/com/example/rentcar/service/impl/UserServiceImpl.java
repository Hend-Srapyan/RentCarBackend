package com.example.rentcar.service.impl;

import com.example.rentcar.dto.SaveUserRequest;
import com.example.rentcar.dto.UserAuthRequest;
import com.example.rentcar.dto.UserAuthResponse;
import com.example.rentcar.entity.User;
import com.example.rentcar.mapper.UserMapper;
import com.example.rentcar.repository.UserRepository;
import com.example.rentcar.service.UserService;
import com.example.rentcar.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.rentcar.exception.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;


    @Override
    public void save(SaveUserRequest user) {
        User entity = userMapper.toEntity(user);
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(entity);
    }

    @Override
    public Optional<User> findByUsername(String email) {
        return userRepository.findByUsername(email);
    }

    @Override
    public ResponseEntity<UserAuthResponse> login(UserAuthRequest userAuthRequest) {
        Optional<User> byUsername = findByUsername(userAuthRequest.getUsername());

        if (byUsername.isEmpty()) {
            throw new UsernameNotFoundException("Username with " + userAuthRequest.getUsername() + " not found");

        }

        User user = byUsername.get();
        if (passwordEncoder.matches(userAuthRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.ok(UserAuthResponse.builder()
                    .token(jwtTokenUtil.generateToken(user.getUsername()))
                    .name(user.getName())
                    .surname(user.getSurname())
                    .userId(user.getId())
                    .build());
        }

        throw new UsernameNotFoundException("Username with " +  userAuthRequest.getUsername() + " not found");
    }

    @Override
    public ResponseEntity<?> register(SaveUserRequest saveUserRequest) {
        if (findByUsername(saveUserRequest.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }
        save(saveUserRequest);
        return ResponseEntity.ok("Registration successful");
    }
}