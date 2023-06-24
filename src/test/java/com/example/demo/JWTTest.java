package com.example.demo;

import com.example.demo.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.security.auth.message.AuthException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@SpringBootTest
public class JWTTest {
    @Test
    public void jwtvalidate() throws AuthException {

        JwtUtil jwtUtil = new JwtUtil();
        Claims claims=jwtUtil.validateToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKYXNvbiIsImV4cCI6MTY4ODA0NjI5Mn0.lhCmbzyS3Ub_CcKMbP6C85PPdEZ_oOzJVNRd1wSgamU");
        System.out.println(claims.getSubject());
    }
    @Test
    public void jwtCreate() throws AuthException {

        JwtUtil jwtUtil = new JwtUtil();
        String jwt =jwtUtil.createJwt("Jason");
        System.out.println(jwt);
    }
    @Test
    public  void passwordEncodeTest(){
        BCryptPasswordEncoder bCryptPasswordEncoder= new BCryptPasswordEncoder();
        String password =bCryptPasswordEncoder.encode("123");
        //$2a$10$0GuPC64zqqI.19vWbh5Q7O/89uarvHgQ5iwoaZnUOMoCgyH0LJkAO
        System.out.println(bCryptPasswordEncoder.matches("123","$2a$10$0GuPC64zqqI.19vWbh5Q7O/89uarvHgQ5iwoaZnUOMoCgyH0LJkAO"));
    }
}
