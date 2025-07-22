package com.example.rentcar.security;

import com.example.rentcar.entity.User;
import com.example.rentcar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Trying to load user by username: {}", username);
        Optional<User> byUsername = userService.findByUsername(username);
        if (byUsername.isPresent()) {
            User userFromDB = byUsername.get();
            return new CurrentUser(userFromDB);
        }
        throw new UsernameNotFoundException("User with " + username + " does not exist");
    }
}