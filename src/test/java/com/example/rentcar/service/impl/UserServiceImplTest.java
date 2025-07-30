package com.example.rentcar.service.impl;

import com.example.rentcar.dto.SaveUserRequest;
import com.example.rentcar.dto.UserAuthRequest;
import com.example.rentcar.dto.UserAuthResponse;
import com.example.rentcar.entity.User;
import com.example.rentcar.exception.UsernameNotFoundException;
import com.example.rentcar.mapper.UserMapper;
import com.example.rentcar.repository.UserRepository;
import com.example.rentcar.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void save_ShouldEncodePasswordAndSaveUser() {
        SaveUserRequest request = new SaveUserRequest();
        request.setPassword("plainPass");
        User user = new User();

        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");

        userService.save(request);

        assertEquals("encodedPass", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenExists() {
        User user = new User();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPass");
        user.setName("John");
        user.setSurname("Doe");
        user.setId(1);

        UserAuthRequest request = new UserAuthRequest();
        request.setUsername("testuser");
        request.setPassword("plainPass");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainPass", "encodedPass")).thenReturn(true);
        when(jwtTokenUtil.generateToken("testuser")).thenReturn("fake-jwt-token");

        ResponseEntity<UserAuthResponse> response = userService.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("fake-jwt-token", response.getBody().getToken());
        assertEquals("John", response.getBody().getName());
        assertEquals("Doe", response.getBody().getSurname());
        assertEquals(1, response.getBody().getUserId());
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        UserAuthRequest request = new UserAuthRequest();
        request.setUsername("nonexistent");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.login(request));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordWrong() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPass");

        UserAuthRequest request = new UserAuthRequest();
        request.setUsername("testuser");
        request.setPassword("wrongPass");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        assertThrows(UsernameNotFoundException.class, () -> userService.login(request));
    }

    @Test
    void register_ShouldReturnConflict_WhenUserExists() {
        SaveUserRequest request = new SaveUserRequest();
        request.setUsername("existing");

        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = userService.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
    }

    @Test
    void register_ShouldSaveUser_WhenNew() {
        SaveUserRequest request = new SaveUserRequest();
        request.setUsername("newuser");
        request.setPassword("plain");

        User mappedUser = new User();

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(passwordEncoder.encode("plain")).thenReturn("encoded");

        ResponseEntity<?> response = userService.register(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Registration successful", response.getBody());
        assertEquals("encoded", mappedUser.getPassword());

        verify(userRepository).save(mappedUser);
    }
}