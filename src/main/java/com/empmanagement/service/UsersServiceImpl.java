package com.empmanagement.service;

import com.empmanagement.common.DataHandleException;
import com.empmanagement.mapper.UsersMapper;
import com.empmanagement.model.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsersServiceImpl implements UsersService {

    private final String PHONE_REGEX = "^09\\d{8}$";
    private static final String SECRET_KEY = "q1w2e3r4t5y6u7i8o9p0q1w2e3r4t5y6u7i8o9p0q1w2e3r4t5y6u7i8o9p0q1w2e3r4t5y6u7i8o9p0";
    private final long EXPIRATION_TIME = 3600000; // 1 小時

    @Autowired
    private UsersMapper usersMapper;


    @Override
    public List<Users> findUserByName(String name) {
        return usersMapper.findByName(name);
    }

    @Override
    public Map<String, String> deleteUserById(List<Integer> idList) {
        Map<String, String> result = new HashMap<>();
        int usersList;
        try {
            usersList = usersMapper.deleteById(idList);
        } catch (RuntimeException e) {
            throw new DataHandleException(e.getMessage());

        }
        if (idList.size() < 1) {
            throw new DataHandleException("請確認要操作的會員");
        }
        if (usersList > 0) {
            result.put("message", "刪除成功");
        } else {
            throw new DataHandleException("刪除失敗,請稍後再試");
        }
        return result;
    }

    @Override
    public Map<String, String> register(Users user) {
        Map<String, String> result = new HashMap<>();
        try {
            if (!user.getPhone().matches(PHONE_REGEX)) {
                throw new DataHandleException("電話格式不正確");
            }
            int id = usersMapper.insert(user);
            if (id > 0) {
                result.put("message", "註冊會員成功");
            }
        } catch (DuplicateKeyException e) {
            String errorMessage = e.getCause().getMessage();
            if (errorMessage.contains("account_ukey")) {
                throw new DataHandleException("帳號已有人使用");
            } else if (errorMessage.contains("phone_ukey")) {
                throw new DataHandleException("電話已有人使用");
            }
        }
        return result;
    }

    @Override
    @Transactional
    public Map<String, String> updateUser(Users user) {
        Map<String, String> result = new HashMap<>();
        Users dbUser = usersMapper.findById(user.getId());
        if (dbUser == null) {
            throw new RuntimeException("此用戶已不存在");
        }
        if (!((dbUser.getVersion()) == (user.getVersion()))) {
            throw new RuntimeException("此用戶已由其他用戶更動過");
        }
        if (!user.getPhone().matches(PHONE_REGEX)) {
            throw new DataHandleException("電話格式不正確");
        }
        int updatedRows = 0;
        try {
            updatedRows = usersMapper.updateUser(user);
        } catch (DuplicateKeyException e) {
            String errorMessage = e.getCause().getMessage();
            if (errorMessage.contains("account_ukey")) {
                throw new DataHandleException("帳號已有人使用");
            } else if (errorMessage.contains("phone_ukey")) {
                throw new DataHandleException("電話已有人使用");
            }
        }
        if (updatedRows > 0) {
            result.put("message", "更新成功");
        } else {
            throw new DataHandleException("更新失敗,請嘗試更換帳號或電話");
        }
        return result;

    }

    @Override
    public Users getUserByAccount(String account, String password) {
        Users user = usersMapper.findByAccount(account, password);
        return user;
    }

    @Override
    public String loginByUser(Users loginUser) {
        Users user = this.getUserByAccount(loginUser.getAccount(), loginUser.getPassword());
        if (user != null && user.getAccount().equals(loginUser.getAccount()) && user.getPassword().equals(loginUser.getPassword())) {
            String token = Jwts.builder()
                    .setSubject(user.getAccount())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                    .compact();
            return token;
        } else {
            throw new DataHandleException("登入失敗,請確認帳號密碼");
        }
    }
}
