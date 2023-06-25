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
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private UserDao userDao;

    @Autowired
    private AuthenticationManager authenticationManager;

    boolean authRequestUserById(int userId){
        //得到安全上下文驗證身分 (防止 此token 取得他人資料)
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null)
            return false;
        int securityContextUserId=((UserToken)authentication.getPrincipal()).getUserId();
        return securityContextUserId ==userId;
    }
    @Override
    public User getUserById(int userId) {

        if(authRequestUserById(userId))
             return userDao.getUserById( userId);
        throw   new ResponseStatusException(HttpStatus.BAD_REQUEST);

    }

    @Override
    public User getCreateUserById(int userId) {//防止剛創立沒有token 異常
        return userDao.getUserById( userId);
    }

    @Override
    public int createUser(UserInsertRequest userInsertRequest) {
        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
        String encodePassword=bCryptPasswordEncoder.encode(userInsertRequest.getPassword());
        userInsertRequest.setPassword(encodePassword);
        System.out.println("創立角色");
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
    @Transactional
    @Override
    public void deleteUser(int userId) {
        userDao.removeUserRoles(userId);//先移除權限
        userDao.deleteUserTokenByID(userId);
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
        //判斷資料庫內有無token(此處雖使用MySQL ,但應使用Redis,須將Token存入Redis)
        UserToken userToken = userDao.getTokenByUserId(Integer.parseInt(userId) );
        JwtUtil jwtUtil = new JwtUtil();

        Claims claim=jwtUtil.validateToken(userToken.getToken());
        //判斷是否過期
        if(userToken!=null&& claim==null){
            String jwt = jwtUtil.createJwt(userId);
            Map<String,String> map=new HashMap<>();
            map.put("token",jwt);
            userDao.updateUserToken(Integer.parseInt(userId) ,jwt);
            return new ResponseResult(200,"登入成功",map);
        }
        //判斷資料庫內有無token
        if(userToken==null){
            //判斷沒此token
            //生成token 存入 db(此處雖使用MySQL ,但應使用Redis,須將Token存入Redis)
            String jwt = jwtUtil.createJwt(userId);

            Map<String,String> map=new HashMap<>();
            map.put("token",jwt);
            userDao.createUserToken(Integer.parseInt(userId) ,jwt);
            return new ResponseResult(200,"登入成功",map);
        }

        Map<String,String> map=new HashMap<>();
        map.put("token",userToken.getToken());
        return new ResponseResult(200,"登入成功",map);
    }

    @Override
    public ResponseResult logout() {
        //取得該筆request 的 SecurityContext (每筆Request都不同)
        UsernamePasswordAuthenticationToken authentication= (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserToken principal= (UserToken)authentication.getPrincipal();
        //刪除 資料庫中的token
        userDao.deleteUserTokenByID(principal.getUserId());
        return new ResponseResult(200,"登出成功",null);
    }
}
