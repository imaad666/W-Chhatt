package com.chatapp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatapp.dto.LoginRequest;
import com.chatapp.dto.UserDto;
import com.chatapp.security.JwtTokenProvider;
import com.chatapp.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto) {
        try {
            UserDto registeredUser = userService.registerUser(userDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", registeredUser);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            // Update last login
            userService.updateLastLogin(loginRequest.getUsername());
            
            // Get user details
            UserDto userDto = userService.findByUsername(loginRequest.getUsername())
                    .map(user -> {
                        UserDto dto = new UserDto();
                        dto.setId(user.getId());
                        dto.setUsername(user.getUsername());
                        dto.setEmail(user.getEmail());
                        dto.setCreatedAt(user.getCreatedAt());
                        dto.setLastLogin(user.getLastLogin());
                        dto.setActive(user.isActive());
                        return dto;
                    })
                    .orElse(null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", jwt);
            response.put("tokenType", "Bearer");
            response.put("username", loginRequest.getUsername());
            response.put("email", userDto != null ? userDto.getEmail() : null);
            response.put("createdAt", userDto != null ? userDto.getCreatedAt() : null);
            
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return userService.findByUsername(username)
                    .map(user -> {
                        UserDto userDto = new UserDto();
                        userDto.setId(user.getId());
                        userDto.setUsername(user.getUsername());
                        userDto.setEmail(user.getEmail());
                        userDto.setCreatedAt(user.getCreatedAt());
                        userDto.setLastLogin(user.getLastLogin());
                        userDto.setActive(user.isActive());
                        return ResponseEntity.ok(userDto);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");
        response.put("details", errors);
        
        return ResponseEntity.badRequest().body(response);
    }
} 