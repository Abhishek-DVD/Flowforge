package com.flowforge.auth.dto;

import java.util.UUID;

//returns user details and JWT after successful authentication.
public record AuthResponse(
    UUID userId,
    String email,
    String fullName,
    String accessToken,
    String tokenType
) {}