package com.flowforge.auth.dto;

import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        UUID userId,
        String email,
        String fullName,
        List<String> roles
) {}