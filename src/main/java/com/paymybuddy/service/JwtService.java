package com.paymybuddy.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {


    @Value("${jwt.key}")
    private String SECRET;

    public String generateToken(String eMail){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, eMail);
    }

    private String createToken(Map<String, Object> claims, String eMail){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(eMail)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        String eMail = extractUser(token);
        Date expirationDate = extractExpiration(token);
        return userDetails.getUsername().equals(eMail) &&!expirationDate.before(new Date());
    }

    private Date extractExpiration(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build().parseClaimsJws(token).getBody();
        return claims.getExpiration();
    }

    public String extractUser(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

}
