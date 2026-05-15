package com.springProject.Practice.controller;

import com.springProject.Practice.dto.LoginRequest;
import com.springProject.Practice.dto.LoginResponse;
import com.springProject.Practice.dto.RegisterRequest;
import com.springProject.Practice.dto.RegisterResponse;
import com.springProject.Practice.model.User;
import com.springProject.Practice.repository.UserRepository;
import com.springProject.Practice.security.JwtService;
import com.springProject.Practice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);
        return  ResponseEntity.ok(response);
    }

}
