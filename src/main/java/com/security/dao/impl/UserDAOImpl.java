package com.security.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.security.dao.UserDAO;
import com.security.model.User;

@Repository
public class UserDAOImpl implements UserDAO {
	
	@Autowired
	private NamedParameterJdbcTemplate nJdbcTemplate;

	@Override
	public User findByUsername(final String userName) {
		String sql = new StringBuilder()
				.append("SELECT u.id, u.password, r.name ")
				.append("FROM mst_user u ")
				.append("LEFT JOIN user_role_assoc ur ON ur.user_id = u.id AND ur.is_active = 1 ")
				.append("LEFT JOIN mst_role r ON ur.role_id = r.id AND r.is_active = 1 ")
				.append("WHERE u.userName =:userName AND u.is_active = 1")
				.toString();
		
		Map<String, String> paramMap = Collections.singletonMap("userName", userName);
		List<User> users = nJdbcTemplate.query(sql, paramMap, new RowMapper<User>() {
			final User user = new User();
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				if(rowNum == 0) {
					Long id = rs.getLong("id");
					String password = rs.getString("password");
					user.setId(id);
					user.setUserName(userName);
					user.setPassword(password);	
					user.setRoles(new ArrayList<String>());
				}
				String role = rs.getString("name");
				user.getRoles().add(role);
				return user;
			}
		});
		return users.isEmpty() ? null : users.get(0);
	}

	@Override
	public List<User> findAll() {
		String sql = new StringBuilder()
				.append("SELECT u.id, u.password, r.name ")
				.append("FROM mst_user u ")
				.append("LEFT JOIN user_role_assoc ur ON ur.user_id = u.id AND ur.is_active = 1 ")
				.append("LEFT JOIN mst_role r ON ur.role_id = r.id AND r.is_active = 1 ")
				.append("WHERE u.is_active = 1")
				.toString();
		List<User> users = nJdbcTemplate.query(sql, new RowMapper<User>() {
			final Map<Long, User> users = new HashMap<Long, User>(); 
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				Long id = rs.getLong("id");
				User user = users.get(id);
				if(null == user) {
					user = new User(id);
					String userName = rs.getString("userName");
					String password = rs.getString("password");
					user.setUserName(userName);
					user.setPassword(password);
					user.setRoles(new ArrayList<String>());
					users.put(id, user);
				}
				String role = rs.getString("name");
				user.getRoles().add(role);
				return user;
			}
		});
		return users;
	}
}
