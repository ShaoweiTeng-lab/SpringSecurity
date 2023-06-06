package com.example.demo.dao;

import com.example.demo.dto.UserInsertRequest;
import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserUpdatePasswordRequest;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.UserToken;

import java.util.List;

public interface UserDao {
    User getUserById(int userId);
    int createUser(UserInsertRequest userInsertRequest);
    User getUserByName(String name);

    void updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest);
    void deleteUser(int userId);
    User login( UserLoginRequest userLoginRequest);

    UserToken getTokenByUserId(int userId);
    void createUserToken(int userId,String token);
    void updateUserToken(int userId,String token);
    void deleteUserTokenByID(int userId);

    //授權
    List<Role> getUserRolesByUserId(int userId);
    void removeUserRoles(int userId);
}
