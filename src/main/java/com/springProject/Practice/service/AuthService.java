package com.springProject.Practice.service;

import com.springProject.Practice.dto.LoginRequest;
import com.springProject.Practice.dto.LoginResponse;
import com.springProject.Practice.dto.RegisterRequest;
import com.springProject.Practice.dto.RegisterResponse;
import com.springProject.Practice.exception.PasswordMismatchException;
import com.springProject.Practice.exception.UserAlreadyExistsException;
import com.springProject.Practice.exception.UserNotFoundException;
import com.springProject.Practice.model.Role;
import com.springProject.Practice.model.User;
import com.springProject.Practice.repository.UserRepository;
import com.springProject.Practice.security.JwtService;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final JwtService jwtService;


    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        // Normalize input
        String name = request.getName().trim();
        String email = request.getEmail().trim().toLowerCase();

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        // Validate password match
        if (request.getConfirmPassword() == null ||
                !request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        // Hash password
        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        // Create entity
        User user = new User(name, email, encodedPassword, Role.USER);

        // Save
        user=userRepository.save(user);
        return new RegisterResponse(
                user.getId(),
                user.getName(),
                user.getEmail()

        );
    }
    public LoginResponse login(LoginRequest request) {
        String email =request.getEmail().trim().toLowerCase();

        User user =
                userRepository.findByEmail(email).orElseThrow(() ->new UserNotFoundException("Invalid email or password"));

        if (!matchesPassword(request.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

    }
    private boolean matchesPassword(String rawPassword,String bCRptPassword){
        return bCryptPasswordEncoder.matches(rawPassword,bCRptPassword);
    }
}
