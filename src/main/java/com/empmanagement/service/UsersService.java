package com.empmanagement.service;

import com.empmanagement.model.Users;

import java.util.List;
import java.util.Map;

public interface UsersService {
//    int addUser(Users user);
    List<Users> findUserByName(String name);
    Map<String, String> deleteUserById(List<Integer> ids);
    Map<String,String> register(Users user);
    Map<String,String> updateUser(Users user);
    Users getUserByAccount(String account ,String password);
}
