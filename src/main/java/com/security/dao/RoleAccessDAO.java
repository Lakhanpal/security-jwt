package com.security.dao;

import java.util.Map;
import java.util.Set;

public interface RoleAccessDAO {
	
	Map<String, Set<String>> loadAllRoleAccesses();

}
