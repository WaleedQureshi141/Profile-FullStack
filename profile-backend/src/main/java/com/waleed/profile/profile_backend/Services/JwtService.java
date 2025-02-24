package com.waleed.profile.profile_backend.Services;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.waleed.profile.profile_backend.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService 
{
    @Value("${jwt.secret}")
    private String secretKey;

    // Creates the Signing Key
    private Key getSigningKey()
    {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generates the token
    public String generateToken(User user)
    {
        return Jwts.builder()
            .claim("userId", user.getUserId())
            .claim("username", user.getUsername())
            .claim("role", user.getRole())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))   // 15 mins
            .signWith(getSigningKey())
            .compact();
    }

    // Decodes the token and extracts claims from it
    public User decodeToken(String token) 
    {
        try 
        {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

            return new User(claims.get("id", Integer.class), claims.get("username", String.class));            
        }
        // checks if token is expired or invalid
        catch (JwtException e) 
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
