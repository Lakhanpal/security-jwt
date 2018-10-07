package com.security.config;

import static com.security.constant.SecurityConstant.AUTH_USER;
import static com.security.constant.SecurityConstant.HEADER_STRING;
import static com.security.constant.SecurityConstant.TOKEN_PREFIX;

import java.io.IOException;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.Gson;
import com.security.exception.SecurityAuthenticationException;
import com.security.exception.SecurityAuthorizationException;
import com.security.model.AuthRequest;
import com.security.model.AuthResponse;
import com.security.service.UserService;
import com.security.util.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private static Logger LOG = LogManager.getLogger();

	@Autowired
    private UserDetailsService userDetailsService;
	
	@Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        AuthRequest authRequest = parseRequest(req);
        String authToken = null;
        if (null != authRequest.getToken()) {
        	LOG.info("token received");
        	if(null == authRequest.getResource()) {
            	throw new SecurityAuthorizationException("no resource provided to access");
            }
            authToken = authRequest.getToken().replace(TOKEN_PREFIX,"");
            
            doAuthenticationWithToken(authRequest);
            LOG.info("token authenticated, now verifying authorization");
            
            doAuthorization(authRequest, req);
            LOG.info("user is authorized to access resource");
            
            res.setHeader(AUTH_USER, authRequest.getUserName());
        } else {
        	LOG.info("token not present in header. peforming first time authentication for user");
            if(null == authRequest.getUserName() || null == authRequest.getPassword()) {
            	LOG.error("invalid login request");
            	throw new SecurityAuthenticationException("invalid login request");
            }
            if(null == authRequest.getResource()) {
            	LOG.info("resource not provided in request. setting default resource to access");
            	authRequest.setResource("/");
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUserName());
            LOG.info("user details loaded from db");
            authToken = jwtTokenUtil.generateToken(userDetails.getUsername());
            authRequest.setToken(authToken);
            LOG.info("token generated, now verifying authorization");
            checkUserAccess(userDetails, authRequest.getResource(), req);
            LOG.info("user is authorized to access resource");
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                    		authRequest.getUserName(),
                    		authRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            res.setHeader(HEADER_STRING, TOKEN_PREFIX+authToken);
        }
        LOG.info("request authentication done!!!");
        res.reset();
        res.setHeader("Content-Type", "application/json;charset=UTF-8");
        AuthResponse authResponse = new AuthResponse(authRequest.getUserName(), authRequest.getToken());
        res.getWriter().write(new Gson().toJson(authResponse));
        res.setStatus(HttpServletResponse.SC_ACCEPTED);
    }
    
    private void doAuthenticationWithToken(AuthRequest authRequest) {
    	String userName = null;
    	String authToken = authRequest.getToken();
    	try {
    		userName = jwtTokenUtil.getUsernameFromToken(authToken);
    		authRequest.setUserName(userName);
        } catch (IllegalArgumentException e) {
            new SecurityAuthenticationException("an error occured during getting username from token", e);
        } catch (ExpiredJwtException e) {
            new SecurityAuthenticationException("the token is expired and not valid anymore", e);
        } catch(SignatureException e){
        	new SecurityAuthenticationException("Authentication Failed. Username or Password not valid.", e);
        }
    }
    
    private void doAuthenticationWithAuthPrinciple(AuthRequest authRequest, UserDetails userDetails) {
    	String authToken = authRequest.getToken();
    	try {
        	jwtTokenUtil.validateToken(authToken, userDetails);
        } catch (IllegalArgumentException e) {
            new SecurityAuthenticationException("an error occured during getting username from token", e);
        } catch (ExpiredJwtException e) {
            new SecurityAuthenticationException("the token is expired and not valid anymore", e);
        } catch(SignatureException e){
        	new SecurityAuthenticationException("Authentication Failed. Username or Password not valid.", e);
        }
    }
    
    private void checkUserAccess(UserDetails userDetails, String resource, HttpServletRequest req) {
		@SuppressWarnings("unchecked")
		Set<SimpleGrantedAuthority> authorities = (Set<SimpleGrantedAuthority>) userDetails.getAuthorities();

		boolean hasAccess = ((UserService) userDetailsService).checkAccess(authorities, resource);
		if (false == hasAccess) {
			throw new SecurityAuthorizationException("forbidden access");
		}
    }
    
    private void doAuthorization(AuthRequest authRequest, HttpServletRequest req) {
    	String userName = authRequest.getUserName();
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	UserDetails userDetails = null;
        if (userName != null && auth == null) {
            userDetails = userDetailsService.loadUserByUsername(userName);
        } else if(auth != null) {
        	userDetails = (UserDetails)auth.getPrincipal();
        }
            
        doAuthenticationWithAuthPrinciple(authRequest, userDetails);

        if(auth == null) {
        	UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
    				userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            LOG.info("authenticated user " + userName + ", setting security context");
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        checkUserAccess(userDetails, authRequest.getResource(), req);
    }
    
    private AuthRequest parseRequest(HttpServletRequest req) throws SecurityAuthenticationException {
    	AuthRequest authRequest = null;
    	try {
			authRequest = new Gson().fromJson(req.getReader(), AuthRequest.class);
		} catch (IOException e) {
			throw new SecurityAuthenticationException("invalid request", e);
		}
    	return authRequest;
    }
}