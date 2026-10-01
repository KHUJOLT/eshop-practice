package com.eshop.khujolt.eshop_khujolt_backend.service;

import com.eshop.khujolt.eshop_khujolt_backend.dto.request.LoginRequest;
import com.eshop.khujolt.eshop_khujolt_backend.dto.request.RegisterRequest;
import com.eshop.khujolt.eshop_khujolt_backend.dto.response.LoginResponse;
import com.eshop.khujolt.eshop_khujolt_backend.dto.response.UserResponse;
import com.eshop.khujolt.eshop_khujolt_backend.entity.Role;
import com.eshop.khujolt.eshop_khujolt_backend.entity.User;
import com.eshop.khujolt.eshop_khujolt_backend.exception.DuplicateResourceException;
import com.eshop.khujolt.eshop_khujolt_backend.exception.ResourceNotFoundException;
import com.eshop.khujolt.eshop_khujolt_backend.repository.UserRepository;
import com.eshop.khujolt.eshop_khujolt_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User();

        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public LoginResponse login(LoginRequest request) {

        System.out.println("LOGIN REQUEST: " + request.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password())
        );

        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        System.out.println("AUTHENTICATED: " + authentication.getName());

        String token = jwtService.generateToken(user);

        return new LoginResponse(token);
    }
}
