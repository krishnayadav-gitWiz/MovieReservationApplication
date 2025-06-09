package com.moviereservation.controller;

import com.moviereservation.dto.UserDto;
import com.moviereservation.model.User;
import com.moviereservation.service.UserService;
import com.moviereservation.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            UserDto registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Incorrect username or password"));
        }
        
        final UserDetails userDetails = userService.loadUserByUsername(username);
        final String jwt = jwtUtil.generateToken(userDetails);
        
        UserDto user = userService.getUserByUsername(username);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("user", user);
        
        return ResponseEntity.ok(response);
    }
}
