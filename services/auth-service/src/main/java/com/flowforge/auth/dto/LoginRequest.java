package com.flowforge.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//receives login details.
public record LoginRequest(

    @NotBlank
    @Email
    String email,

    @NotBlank
    String password

) {}