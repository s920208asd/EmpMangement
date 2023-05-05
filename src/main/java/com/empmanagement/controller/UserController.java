package com.empmanagement.controller;

import com.empmanagement.model.Users;
import com.empmanagement.service.UsersServiceImpl;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final String PHONE_REGEX = "^09\\d{8}$";
    private static final String SECRET_KEY = "q1w2e3r4t5y6u7i8o9p0q1w2e3r4t5y6u7i8o9p0q1w2e3r4t5y6u7i8o9p0q1w2e3r4t5y6u7i8o9p0";
    private final long EXPIRATION_TIME = 3600000; // 1 小時

    @Autowired
    private UsersServiceImpl usersService;

    @GetMapping
    public ResponseEntity<List<Users>> getUsersByName(@RequestParam String name) {
        List<Users> usersList = usersService.findUserByName(name);
        return ResponseEntity.ok(usersList);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteUserById(@RequestBody List<Integer> ids) {

        Map<String, String> result = new HashMap<>();
        if (ids.size() > 0) {
            result = usersService.deleteUserById(ids);
        } else {
            result.put("message", "請確認要操作的會員");
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Users loginUser, HttpSession session) {
        Users user = usersService.getUserByAccount(loginUser.getAccount(), loginUser.getPassword());
        if (user != null && user.getAccount().equals(loginUser.getAccount()) && user.getPassword().equals(loginUser.getPassword())) {
            String token = Jwts.builder()
                    .setSubject(user.getAccount())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                    .compact();
            session.setAttribute("jwt", token);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.ok("登入失敗,請確認帳號密碼");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Users user) {
        Map<String, String> result = new HashMap<>();
        String name = user.getUsername();
        if (name == null) {
            System.out.println("Name is null");
        } else if (name.equals("null")){
            System.out.println("Name is: " + name);
        }
//        StringBuilder message = new StringBuilder();
//        if (user.getAccount() == null ||user.getAccount().equals("null")|| user.getAccount().equals("") || user.getAccount().isEmpty()) {
//            message.append("帳號不能為空");
//        }
//        if (user.getUsername() == null || user.getUsername().equals("null")||user.getUsername().equals("") || user.getUsername().isEmpty()) {
//            message.append(",會員名不能為空");
//        }
//        if (!user.getPhone().matches(PHONE_REGEX)) {
//            message.append(",電話格式不正確");
//        }
//        if (user.getPhone() == null ||user.getPhone().equals("null")|| user.getPhone().equals("") || user.getPhone().isEmpty()) {
//            message.append(",電話不能為空");
//        } else {
//            result = usersService.register(user);
//        }
//        if (!(message.length() > 0)) {
//            if (message.indexOf(",") == 0) {
//                message.deleteCharAt(0);
//            }
//            result.put("message", message.toString());
//        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> update(@RequestBody Users user) {
        Map<String, String> result = new HashMap();
        StringBuilder message = new StringBuilder();
        if (user.getAccount() == null ||user.getAccount().equals("null")|| user.getAccount().equals("") || user.getAccount().isEmpty()) {
            message.append("帳號不能為空");
        }
        if (user.getUsername() == null || user.getUsername().equals("null")||user.getUsername().equals("") || user.getUsername().isEmpty()) {
            message.append(",會員名不能為空");
        }
        if (!user.getPhone().matches(PHONE_REGEX)) {
            message.append(",電話格式不正確");
        }
        if (user.getPhone() == null ||user.getPhone().equals("null")|| user.getPhone().equals("") || user.getPhone().isEmpty()) {
            message.append(",電話不能為空");
        } else {
            result = usersService.updateUser(user);
        }
        if (!(message.length() > 0)) {
            if (message.indexOf(",") == 0) {
                message.deleteCharAt(0);
            }
            result.put("message", message.toString());
        }
        return ResponseEntity.ok(result);
    }
}

