package com.example.rentcar.endpoint;

import com.example.rentcar.dto.SaveUserRequest;
import com.example.rentcar.dto.UserAuthRequest;
import com.example.rentcar.dto.UserAuthResponse;
import com.example.rentcar.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserEndpoint {

    private final UserService userService;

    @PostMapping("/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Username or password does not exist"),
            @ApiResponse(responseCode = "200",description = "Login is success")
    })
    public ResponseEntity<UserAuthResponse> login(@RequestBody UserAuthRequest userAuthRequest) {
       return userService.login(userAuthRequest);
    }

    @PostMapping("/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Username already exists"),
            @ApiResponse(responseCode = "200",description = "Registration is success")
    })
    public ResponseEntity<?> register(@RequestBody SaveUserRequest saveUserRequest) {
        return userService.register(saveUserRequest);
    }
}