package com.security.exception;

import org.springframework.security.access.AccessDeniedException;

public class SecurityAuthorizationException extends AccessDeniedException {
	
	public SecurityAuthorizationException(String msg) {
		super(msg);
	}

	public SecurityAuthorizationException(String msg, Exception e) {
		super(msg, e);
	}
}
