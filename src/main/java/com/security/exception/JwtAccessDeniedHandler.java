package com.security.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler, Serializable {

	private static Logger LOG = LogManager.getLogger();
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		LOG.info("JwtAuthenticationEntryPoint::: "+accessDeniedException.getMessage());
		response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
	}

   
}