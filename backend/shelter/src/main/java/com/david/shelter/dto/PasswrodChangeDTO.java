package com.david.shelter.dto;

public record PasswrodChangeDTO (
    String oldPassword,
    String newPassword,
    String newPasswordAgain
) {}
