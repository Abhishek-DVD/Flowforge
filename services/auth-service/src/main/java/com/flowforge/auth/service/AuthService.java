package com.flowforge.auth.service;

import com.flowforge.auth.dto.AuthResponse;
import com.flowforge.auth.dto.LoginRequest;
import com.flowforge.auth.dto.RegisterRequest;
import com.flowforge.auth.dto.UserProfileResponse;
import com.flowforge.auth.entity.Role;
import com.flowforge.auth.entity.User;
import com.flowforge.auth.exception.ResourceConflictException;
import com.flowforge.auth.exception.ResourceNotFoundException;
import com.flowforge.auth.exception.AccountDisabledException;
import com.flowforge.auth.exception.InvalidCredentialsException;
import com.flowforge.auth.repository.RoleRepository;
import com.flowforge.auth.repository.UserRepository;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Check whether the email already exists.
// Fetch the default ROLE_USER role.
// Hash the password using BCrypt.
// Create the user entity.
// Associate the role.
// Save the user and role association in PostgreSQL.
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceConflictException("Email is already registered");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setEnabled(true);
        user.getRoles().add(userRole);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    // This ensures the lazy-loaded roles are accessible while generating the token.
    // what is lazy loading?
    // Lazy loading is a design pattern commonly used in programming, particularly
    // in the context of object-relational mapping (ORM) frameworks. It refers to
    // the practice of delaying the loading or initialization of an object or
    // resource until it is actually needed, rather than loading it upfront. This
    // can help improve performance and reduce memory usage, especially when dealing
    // with large datasets or complex object graphs.
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new AccountDisabledException("User account is disabled");
        }

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                roles);

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                token,
                "Bearer");
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                roles);
    }
}