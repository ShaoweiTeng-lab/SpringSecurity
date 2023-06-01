package com.example.demo.service;

import com.example.demo.dto.ResponseResult;
import com.example.demo.dto.UserInsertRequest;
import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserUpdatePasswordRequest;
import com.example.demo.model.User;

public interface UserService {
    User getUserById(int userId);
    int createUser(UserInsertRequest userInsertRequest);

    User getUserByName(String name);
    void updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest);
    void deleteUser(int userId);

    ResponseResult login(UserLoginRequest userLoginRequest);
    ResponseResult logout();
}
