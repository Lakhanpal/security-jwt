package com.security.dao;

import java.util.List;

import com.security.model.User;


public interface UserDAO {
	
    User findByUsername(String username);

    List<User> findAll();
}
