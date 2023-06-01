package com.example.demo.controller;

import com.example.demo.dto.ResponseResult;
import com.example.demo.dto.UserInsertRequest;
import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserUpdatePasswordRequest;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.validation.Valid;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @GetMapping("/test")
    public  String Test(){

        JwtUtil jwtUtil=new JwtUtil();
        Map<String,String>  claims =new HashMap<>();
        claims.put("userName","1");
        String jwt = jwtUtil.generateToken(claims);

        return jwt;
    }
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId")int userId){
        User user= userService.getUserById(userId);
        if(user==null)
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return  ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserInsertRequest userInsertRequest){
        int userId= userService.createUser(userInsertRequest);
        User user= userService.getUserById(userId);
        return  ResponseEntity.status(HttpStatus.OK).body(user);
    }
    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserUpdatePasswordRequest userUpdatePasswordRequest){
        userService.updatePassword(userUpdatePasswordRequest);
        return  ResponseEntity.status(HttpStatus.OK).body("更新成功");
    }
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") int userId){
        userService.deleteUser(userId);
        return  ResponseEntity.status(HttpStatus.OK).body("刪除成功");
    }

    @PostMapping(value = "/users/login" )
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequest userLoginRequest)throws Exception{

        ResponseResult responseResult= userService.login(userLoginRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        String rs =objectMapper.writeValueAsString(responseResult);
        return  ResponseEntity.status(HttpStatus.OK).body(responseResult );
    }
    @GetMapping("/convert")
    public  String convert() throws JsonProcessingException {
        User user = new User();
        user.setUserId(1);
        user.setUserName("Jason");
        user.setUserPassword("Ha");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(user);
    }

    @GetMapping("/getUser")
    public  User getUser() throws JsonProcessingException {
        String s ="{\"userId\":1,\"userName\":\"Jason\",\"userPassword\":\"Ha\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        User user=objectMapper.readValue(s, User.class);
        System.out.println(user.getUserId());
        System.out.println(user.getUserName());
        System.out.println(user.getUserPassword());
        return  user;
    }
}
