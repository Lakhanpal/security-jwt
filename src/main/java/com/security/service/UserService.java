package com.security.service;

import com.security.model.User;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public interface UserService {

    List<User> findAll();
    User findOne(String username);
    boolean checkAccess(Set<SimpleGrantedAuthority> authorities, String resource);
}
