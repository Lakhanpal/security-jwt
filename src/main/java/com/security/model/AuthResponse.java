package com.security.model;

public class AuthResponse {

	private String userName;

	private String token;

	public AuthResponse(String userName, String token) {
		super();
		this.userName = userName;
		this.token = token;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
