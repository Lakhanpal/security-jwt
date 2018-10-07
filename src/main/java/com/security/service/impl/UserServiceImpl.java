package com.security.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.security.dao.RoleAccessDAO;
import com.security.dao.UserDAO;
import com.security.model.User;
import com.security.service.UserService;


@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {
	
	private static Logger LOG = LogManager.getLogger();
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private RoleAccessDAO roleAccessDAO;
	
	//private static final ConcurrentHashMap<String, Set<RoleResourceAccess>> ROLE_RESOURCE_ACCESSES = new ConcurrentHashMap<String, Set<RoleResourceAccess>>();
	
	private ConcurrentHashMap<SimpleGrantedAuthority, Set<String>> ROLE_RESOURCES = null;
	
	@PostConstruct
	public void loadRoleAccesses() {
		LOG.info("loading all role-accesses...");
		Map<String, Set<String>> map = roleAccessDAO.loadAllRoleAccesses();
		ROLE_RESOURCES = map.entrySet().stream()
				.map(new Function<Map.Entry<String, Set<String>>, Map.Entry<SimpleGrantedAuthority, Set<String>>>() {
					@Override
					public Entry<SimpleGrantedAuthority, Set<String>> apply(Entry<String, Set<String>> e) {
						return new Map.Entry<SimpleGrantedAuthority, Set<String>>() {

							@Override
							public SimpleGrantedAuthority getKey() {
								return new SimpleGrantedAuthority(e.getKey());
							}

							@Override
							public Set<String> getValue() {
								return e.getValue();
							}

							@Override
							public Set<String> setValue(Set<String> value) {
								return value;
							}
						};
					}
				}).collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue, (k, v) -> v,
						ConcurrentHashMap::new));
		LOG.info("all role-accesses loaded...");
	}
	
	private List<SimpleGrantedAuthority> getAuthority(User user) {
		return user.getRoles().stream().map(new Function<String, SimpleGrantedAuthority>() {
			@Override
			public SimpleGrantedAuthority apply(String t) {
				return new SimpleGrantedAuthority(t);
			}
			
		}) .collect(Collectors.toList());
	}

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		User user = userDAO.findByUsername(userName);
		if(user == null) {
			LOG.error("userName " + userName + " not found in database");
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		List<SimpleGrantedAuthority> auths = getAuthority(user);
		System.out.println(auths);
		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), getAuthority(user));
	}

	@Override
	public List<User> findAll() {
		List<User> list = new ArrayList<>();
		userDAO.findAll().iterator().forEachRemaining(list::add);
		return list;
	}

	@Override
	public User findOne(String userName) {
		return userDAO.findByUsername(userName);
	}

	@Override
	public boolean checkAccess(Set<SimpleGrantedAuthority> authorities, String resource) {
		//RoleResourceAccess roleResourceAccess = new RoleResourceAccess(authority, resource);
		//Set<RoleResourceAccess> roleResourceAccesses = ROLE_RESOURCE_ACCESSES.get(resource);
		for( SimpleGrantedAuthority authority : authorities ) {
			Set<String> resources = ROLE_RESOURCES.get(authority);
			if(resources.contains(resource)) {
				return true;
			}
		}
		return false;
	}
	
}
