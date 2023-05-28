package com.example.demo.service.imp;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.ResponseResult;
import com.example.demo.dto.UserInsertRequest;
import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserUpdatePasswordRequest;
import com.example.demo.model.User;
import com.example.demo.model.UserToken;
import com.example.demo.security.UserDetailsImp;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.message.AuthException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UserServiceImp  implements UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public User getUserById(int userId) {
        return userDao.getUserById( userId);
    }

    @Override
    public int createUser(UserInsertRequest userInsertRequest) {
        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
        String encodePassword=bCryptPasswordEncoder.encode(userInsertRequest.getPassword());
        userInsertRequest.setPassword(encodePassword);
        return userDao.createUser( userInsertRequest);
    }

    @Override
    public User getUserByName(String name) {
        return userDao.getUserByName( name);
    }

    @Override
    public void updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest) {
        User user= userDao.getUserByName( userUpdatePasswordRequest.getUserName());
        if(user==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        userDao.updatePassword( userUpdatePasswordRequest);
    }

    @Override
    public void deleteUser(int userId) {
        User user= userDao.getUserById( userId);
        if(user==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        userDao.deleteUser(userId);
    }

    @Transactional
    @Override
    public ResponseResult login(UserLoginRequest userLoginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =new UsernamePasswordAuthenticationToken(userLoginRequest.getUserName(),userLoginRequest.getPassword());
        Authentication authentication= authenticationManager.authenticate(authenticationToken);//會自動調用 ProvideManager 然後調用 UserDetailsService進行驗證
        if(Objects.isNull(authentication))//傳回空值代表認證失敗
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        //通過 使用userId生成 jwt ，返回jwt
        UserDetailsImp userDetail = (UserDetailsImp) authentication.getPrincipal();
        String userId =String.valueOf( userDetail.getUser().getUserId());
        //
        //判斷資料庫內有無token
        UserToken userToken = userDao.getTokenByUserId(Integer.parseInt(userId) );
        if (!Objects.isNull(userToken)){
            Map<String,String> map=new HashMap<>();
            map.put("token",userToken.getToken());
            return new ResponseResult(200,"登入成功",map);
        }

        JwtUtil jwtUtil = new JwtUtil();

        //判斷是否過期
        if(jwtUtil.validateToken(userToken.getToken())==null){
            String jwt = jwtUtil.createJwt(userId);
            Map<String,String> map=new HashMap<>();
            map.put("token",jwt);
            userDao.updateUserToken(Integer.parseInt(userId) ,jwt);
            return new ResponseResult(200,"登入成功",map);
        }
        //生成token 存入 db
        String jwt = jwtUtil.createJwt(userId);

        Map<String,String> map=new HashMap<>();
        map.put("token",jwt);
        userDao.createUserToken(Integer.parseInt(userId) ,jwt);
        return new ResponseResult(200,"登入成功",map);
    }
}
