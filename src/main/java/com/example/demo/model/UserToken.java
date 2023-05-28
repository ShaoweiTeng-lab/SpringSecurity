package com.example.demo.model;

import lombok.Data;

@Data
public class UserToken {
    int tokenId;
    int userId;
    String token;
}
