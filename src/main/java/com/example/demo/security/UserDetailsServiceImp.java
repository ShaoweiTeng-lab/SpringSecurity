package com.example.demo.security;

import com.example.demo.dao.UserDao;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
    @Autowired
    UserDao userDao;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =userDao.getUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("無此user");
        }
        System.out.println("執行認證");
        List<Role> roles=userDao.getUserRolesByUserId(user.getUserId());//從DAO 拿到對應權限
        //查詢對應權限
        List<String> permissionsList=new ArrayList();
        for (Role role: roles) {
            permissionsList.add(role.getRoleName());
        }
        return new UserDetailsImp(user,permissionsList);
    }
}
