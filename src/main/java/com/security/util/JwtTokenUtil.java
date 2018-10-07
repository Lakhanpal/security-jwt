package com.security.util;

import static com.security.constant.SecurityConstant.ACCESS_TOKEN_VALIDITY_SECONDS;
import static com.security.constant.SecurityConstant.SIGNING_KEY;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.security.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(User user) {
        return doGenerateToken(user.getUserName());
    }
    
    public String generateToken(String userName) {
        return doGenerateToken(userName);
    }

    private String doGenerateToken(String subject) {

        Claims claims = Jwts.claims().setSubject(subject);
        //claims.put("scopes", Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"))); // additional info

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("http://getbettertool.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS*1000))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .compact();
    }

    public void validateToken(String token, UserDetails userDetails) throws RuntimeException {
        final String username = getUsernameFromToken(token);
        if(username.equals(userDetails.getUsername())) {
        	throw new IllegalArgumentException("token is not associated with username");
        }
        if(isTokenExpired(token)) {
        	throw new ExpiredJwtException(Jwts.header(), Jwts.claims(), "the token is expired and not valid anymore");
        }
    }

}
