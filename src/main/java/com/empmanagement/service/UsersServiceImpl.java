package com.empmanagement.service;

import com.empmanagement.mapper.UsersMapper;
import com.empmanagement.model.Users;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersMapper usersMapper;


    @Override
    public List<Users> findUserByName(String name) {
        return usersMapper.findByName(name);
    }

    @Override
    public Map<String, String> deleteUserById(List<Integer> idList) {
        Map<String, String> result = new HashMap<>();
        int usersList = usersMapper.deleteById(idList);
        if (usersList > 0) {
            result.put("message", "刪除成功");
        } else {
            result.put("message", "刪除失敗,請稍後再試");
        }
        return result;
    }

    @Override
    public Map<String, String> register(Users user) {
        Map<String, String> result = new HashMap<>();
        try {
            int id = usersMapper.insert(user);
            if (id > 0) {
                result.put("message", "註冊會員成功");
            }
        } catch (DuplicateKeyException e) {
            String errorMessage = e.getCause().getMessage();

            if (errorMessage.contains("account_ukey")) {
                result.put("message", "帳號已有人使用");
            } else if (errorMessage.contains("phone_ukey")) {
                result.put("message", "電話已有人使用");
            }
        }
        return result;
    }

    @Override
    @Transactional
    public Map<String, String> updateUser(Users user) {
        Map<String, String> result = new HashMap<>();
        Users dbUser = usersMapper.findById(user.getId());
        try {
            if (dbUser == null) {
                throw new RuntimeException("此用戶已不存在");
            }
            if (!((dbUser.getVersion()) == (user.getVersion()))) {
                throw new RuntimeException("此用戶已由其他用戶更動過");
            }
            int updatedRows = 0;
            try {
                updatedRows = usersMapper.updateUser(user);
            } catch (DuplicateKeyException e) {
                String errorMessage = e.getCause().getMessage();

                if (errorMessage.contains("account_ukey")) {
                    result.put("message", "帳號已有人使用");
                } else if (errorMessage.contains("phone_ukey")) {
                    result.put("message", "電話已有人使用");
                }
                return result;
            }
            if (updatedRows > 0) {
                result.put("message", "更新成功");
            } else {
                result.put("message", "更新失敗,請嘗試更換帳號或電話");
            }
        } catch (RuntimeException e) {
            result.put("message", e.getMessage());
        }
        return result;

    }

    @Override
    public Users getUserByAccount(String account,String password) {
        Users user = usersMapper.findByAccount(account,password);
        return user;
    }
}
