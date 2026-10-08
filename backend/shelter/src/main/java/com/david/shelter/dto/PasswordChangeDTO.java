package com.david.shelter.dto;

public record PasswordChangeDTO (
    String oldPassword,
    String newPassword,
    String newPasswordAgain
) {}
