package com.security.exception;

import org.springframework.security.core.AuthenticationException;

public class SecurityAuthenticationException extends AuthenticationException {
	
	public SecurityAuthenticationException(String msg) {
		super(msg);
	}

	public SecurityAuthenticationException(String msg, Exception e) {
		super(msg, e);
	}
}
