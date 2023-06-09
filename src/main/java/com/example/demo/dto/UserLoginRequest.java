package com.example.demo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class UserLoginRequest {
    @NotBlank
    private String userName;
    @NotBlank
    private String password;
}
