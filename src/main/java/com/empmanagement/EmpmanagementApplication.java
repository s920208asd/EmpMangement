package com.empmanagement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.empmanagement.mapper")
@SpringBootApplication
public class EmpmanagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmpmanagementApplication.class, args);
    }

}
