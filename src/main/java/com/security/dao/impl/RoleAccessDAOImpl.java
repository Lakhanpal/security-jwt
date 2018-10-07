package com.security.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.security.dao.RoleAccessDAO;

@Repository
public class RoleAccessDAOImpl implements RoleAccessDAO {
	
	@Autowired
	private NamedParameterJdbcTemplate nJdbcTemplate;

	@Override
	public Map<String, Set<String>> loadAllRoleAccesses() {
		String sql = new StringBuilder()
				.append("SELECT r.name, rra.resource ")
				.append("FROM role_resource_assoc rra ")
				.append("JOIN mst_role r ON r.id = rra.role_id ")
				.append("WHERE r.is_active = 1 AND rra.is_active = 1")
				.toString();
		return nJdbcTemplate.query(sql, new ResultSetExtractor<Map<String, Set<String>>>() {

			@Override
			public Map<String, Set<String>> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, Set<String>> map = new HashMap<String, Set<String>>();
				while(rs.next()) {
					String name = rs.getString("name");
					String resource = rs.getString("resource");
					if(map.containsKey(name)) {
						map.get(name).add(resource);
					} else {
						Set<String> set = new HashSet<String>();
						set.add(resource);
						map.put(name, set);
					}
				}
				return map;
			}
			
		});
	}

}
