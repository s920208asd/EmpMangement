package com.empmanagement.service;

import com.empmanagement.model.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;

@SpringBootTest
public class UserServiceimplTest {
    @Autowired
    private UsersService usersService;

    @Test
    public void testInsertUser() {
        Users users = new Users();
        users.setAccount("te1st");
        users.setUsername("test");
        users.setPassword("test");
        users.setPhone("1234567890");
        try {
            usersService.addUser(users);
        } catch (DuplicateKeyException e) {
            String errorMessage = e.getCause().getMessage();
            System.out.println(errorMessage);
            if (errorMessage.contains("account_ukey")) {
                System.out.println("只有account");
            } else if (errorMessage.contains("phone_ukey")) {
                System.out.println("只有phone");
            }
        }
    }
}