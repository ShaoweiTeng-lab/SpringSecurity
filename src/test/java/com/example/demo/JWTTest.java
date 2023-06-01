package com.example.demo;

import com.example.demo.utils.JwtUtil;
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
        assert 5>1;
        jwtUtil.validateToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI3IiwiZXhwIjoxNjg1NTAwMDY3fQ.7CoIi1y4eIBkNDZ8OvrD2WsPYaaFNAyaXdbKTDS6blk");
    }
}
