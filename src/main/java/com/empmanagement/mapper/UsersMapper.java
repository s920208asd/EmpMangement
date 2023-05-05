package com.empmanagement.mapper;

import com.empmanagement.model.Users;
import org.apache.ibatis.annotations.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UsersMapper {

    int insert(Users users);

    Users findById(Integer id);

    List<Users> findByName(String name);

    Users findByAccount(String account , String password);

    int deleteById(List<Integer> ids);

    int updateUser(Users user);


}
