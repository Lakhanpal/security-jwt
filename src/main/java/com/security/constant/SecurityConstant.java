package com.security.constant;

public interface SecurityConstant {

	long ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 60 * 60;
	
	String SIGNING_KEY = "gbt123r";
	
	String TOKEN_PREFIX = "Bearer ";
	
	String HEADER_STRING = "Authorization";
	
	String AUTH_USER = "X-Auth-Username";
}
