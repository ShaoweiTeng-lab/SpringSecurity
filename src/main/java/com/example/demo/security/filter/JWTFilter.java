package com.example.demo.security.filter;

import com.example.demo.dao.UserDao;
import com.example.demo.model.User;
import com.example.demo.model.UserToken;
import com.example.demo.security.UserDetailsServiceImp;
import com.example.demo.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.message.AuthException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {
    @Autowired
    UserDao userDao;
    @Autowired
    UserDetailsServiceImp userDetailsServiceImp;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if(!StringUtils.hasText(token)) {
            //放行  因為沒傳入security contextHolder ，其他過濾器會擋掉
            filterChain.doFilter(request,response);
            return;
        }
        String  userId=null;
        JwtUtil jwt =new JwtUtil();

        Claims claims= jwt.validateToken(token);
        if(claims==null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().println("Token null");
            return;
        }
        userId=claims.getSubject();
        //之後從redisCache取得
        UserToken userToken = userDao.getTokenByUserId(Integer.parseInt(userId));

        if(!userToken.getToken().equals(token)){
            response.setContentType("application/json");

            response.getWriter().println("Token error");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }//判斷token 與 db上相同

        User user = userDao.getUserById(Integer.parseInt(userId));
        //存入SecurityContextHolder
        //取得權限userDetailsServiceImp.loadUserByUsername(user.getUserName()).getAuthorities() 需改成從redis 取得 否則每次請求都跟mysql 拿
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =new UsernamePasswordAuthenticationToken(userToken,null,userDetailsServiceImp.loadUserByUsername(user.getUserName()).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        //Filter 傳入 Attribute
        request.setAttribute("userID",user.getUserId());
        System.out.println("setAttribute : "+ request.getAttribute("userID"));

        filterChain.doFilter(request,response);


    }
}
