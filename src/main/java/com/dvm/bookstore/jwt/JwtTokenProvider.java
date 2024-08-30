package com.dvm.bookstore.jwt;

import io.jsonwebtoken.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JwtTokenProvider
 */
@Component
public class JwtTokenProvider {
    private static Logger LOGGER = LogManager.getLogger(JwtTokenProvider.class);
    @Value("${dvm.secret}")
    private String jwtSecret;

    @Value("${dvm.expiration}")
    private int jwtExpiration;
    /**
     * generateToken
     * @param userName
     * @return token
     */
    public String generateToken(String userName){
        Date date =new Date();
        Date expiration = new Date(date.getTime()+jwtExpiration);
        return Jwts.builder()
            .setSubject(userName)
            .setIssuedAt(new Date())
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS512,jwtSecret).compact();
    }
    /**
     * getUserNameFromJwt
     * @param token
     * @return userName
     */
    public String getUserNameFromJwt(String token){
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        }catch (ExpiredJwtException e){
            return null;
        }
    }
    /**
     * validateJwtToken
     * @param authToken
     * @return boolean
     */
    public boolean validateJwtToken(String authToken){
        try{
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
