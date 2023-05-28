package com.example.demo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserUpdatePasswordRequest {
    @NotBlank
    String userName;
    @NotBlank
    String newPassword;
}
