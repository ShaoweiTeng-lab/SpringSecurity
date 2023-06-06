package com.example.demo;

import com.example.demo.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.security.auth.message.AuthException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@SpringBootTest
public class JWTTest {
    @Test
    public void jwtvalidate() throws AuthException {

        JwtUtil jwtUtil = new JwtUtil();
        Claims claims=jwtUtil.validateToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4IiwiZXhwIjoxNjg2MDMwNDI0fQ.0co3sUPA8quOx6bq3r90ioly5wjr8ah5_DejtbZHtxU");
        System.out.println(claims.getSubject());
    }
}
