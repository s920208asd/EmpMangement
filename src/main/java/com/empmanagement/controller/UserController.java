package com.empmanagement.controller;

import com.empmanagement.common.DataHandleException;
import com.empmanagement.model.Users;
import com.empmanagement.service.UsersServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

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
        result = usersService.deleteUserById(ids);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Users loginUser, HttpSession session) {
        String token = usersService.loginByUser(loginUser);
        session.setAttribute("jwt", token);
        Map<String, String> result = new HashMap<>();
        result.put("token" , token);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Users user) {
        Map<String, String> result = new HashMap<>();
        try {
            result = usersService.register(user);
        } catch (RuntimeException e) {
            throw new DataHandleException(e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> update(@RequestBody Users user) {
        Map<String, String> result = new HashMap();
        try {
            result = usersService.updateUser(user);
        } catch (RuntimeException e) {
            throw new DataHandleException(e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}

