package com.example.demo.dao.imp;

import com.example.demo.dao.UserDao;
import com.example.demo.dao.rowMapper.UserRowMapper;
import com.example.demo.dao.rowMapper.UserTokenRowMapper;
import com.example.demo.dto.UserInsertRequest;
import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserUpdatePasswordRequest;
import com.example.demo.model.User;
import com.example.demo.model.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDaoImp implements UserDao {
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public User getUserById(int userId) {
        String sql="SELECT * FROM USER WHERE user_id =:userId";
        Map<String,Object> map =new HashMap<>();
        map.put("userId",userId);
        List<User> rsList= namedParameterJdbcTemplate.query(sql,map,new UserRowMapper());
        if(rsList.size()>0)
            return rsList.get(0);
        return null;
    }

    @Override
    public int createUser(UserInsertRequest userInsertRequest) {
        String sql="INSERT INTO USER (userName,password) " +
                "values(:userName,:password);";
        Map<String,Object> map =new HashMap<>();
        map.put("userName",userInsertRequest.getUserName());
        map.put("password",userInsertRequest.getPassword());
        KeyHolder key=new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql,new MapSqlParameterSource(map),key);
        int userId=key.getKey().intValue();
        return userId;
    }

    @Override
    public User getUserByName(String name) {
        String sql="SELECT * FROM USER WHERE userName =:userName";
        Map<String,Object> map =new HashMap<>();
        map.put("userName",name);
        List<User> rsList= namedParameterJdbcTemplate.query(sql,map,new UserRowMapper());
        if(rsList.size()>0)
            return rsList.get(0);
        return null;
    }

    @Override
    public void updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest) {
        String  sql="UPDATE USER SET password =:password " +
                "where userName =:userName; ";
        Map<String,Object> map =new HashMap<>();
        map.put("userName",userUpdatePasswordRequest.getUserName());
        map.put("password",userUpdatePasswordRequest.getNewPassword());
        namedParameterJdbcTemplate.update(sql,new MapSqlParameterSource(map) );
    }

    @Override
    public void deleteUser(int userId) {
        String sql="DELETE FROM user where user_id =:userId";
        Map<String,Object> map =new HashMap<>();
        map.put("userId",userId );
        namedParameterJdbcTemplate.update(sql,new MapSqlParameterSource(map));
    }

    @Override
    public User login(UserLoginRequest userLoginRequest) {
        String sql="SELECT * FROM USER " +
                "WHERE username =:userName && password =:password";
        Map<String,Object> map =new HashMap<>();
        map.put("userName",userLoginRequest.getUserName());
        map.put("password",userLoginRequest.getPassword());
        List<User> rsList= namedParameterJdbcTemplate.query(sql,map,new UserRowMapper());
        if(rsList.size()>0)
            return rsList.get(0);
        return null;
    }

    @Override
    public UserToken getTokenByUserId(int userId) {
        String sql="SELECT * FROM usertoken " +
                "WHERE user_Id =:userId ";
        Map<String,Object> map =new HashMap<>();
        map.put("userId",userId);
        List<UserToken> rsList=namedParameterJdbcTemplate.query(sql, map, new UserTokenRowMapper());
        if(rsList.size()>0)
            return rsList.get(0);
        return null;
    }

    @Override
    public void createUserToken(int userId, String token) {
        String sql ="INSERT INTO usertoken(user_Id,token) VALUES(:userId,:token )";
        Map map =new HashMap<>();
        map.put("userId",userId);
        map.put("token",token);
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void updateUserToken(int userId, String token) {
        String sql ="Update usertoken set token =:token where user_Id = :userId";
        Map<String,Object> map =new HashMap<>();
        map.put("userId", userId);
        map.put("token", token);
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void deleteUserTokenByID(int userId) {
        String sql ="delete from usertoken where user_Id = :userId";
        Map<String, Object> map =new HashMap<>();
        map.put("userId", userId);
        namedParameterJdbcTemplate.update(sql, map);
    }
}
