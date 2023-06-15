package com.blackops.securitydemo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
    // 256 bit encryption
    private static final String ENCRYPT_KEY = "2B4B6250645367566B5970337336763979244226452948404D6351665468576D";

    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    //claimsTFunction : function that operates on the Claims object and returns a value of type T
    public  <T>T extractClaim(String token, Function<Claims, T>claimsTFunction ){
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public List<String> extractRoleClaims(String token) {
        return (List<String>) extractAllClaims(token).get("roles");
    }

    public boolean isValidToken(String token, UserDetails userDetails){
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

//    public String generateToken(UserDetails userDetails){
//        return generateToken(new HashMap<>(), userDetails);
//    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Prepare a list to hold the authorities.
        List<String> authoritiesList = new ArrayList<>();

        // Loop over all authorities of the user and add them to the list.
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            authoritiesList.add(authority.getAuthority());
        }

        // Add the list of authorities to the claims.
        claims.put("roles", authoritiesList);
        return generateToken(claims, userDetails);
    }



    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(ENCRYPT_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
