package com.course_graph.token;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class JwtProvider {
    @Value("${jwt.secret.key}")
    private String tempraryString;
    private static String secretKey;
    private static final long tokenValidTime = 30 * 60 * 1000L;  // 30 minutes
    public static final String authorizationHeader = "Authorization";
    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(tempraryString.getBytes());
    }

    public static String createToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date(System.currentTimeMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        token = token.split(" ")[1].trim();
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmail(String token) {

        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String jwt) {
        if (jwt == null ||jwt.length() < 8 || !jwt.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER "))
            return false;
        return !isExpired(jwt);
    }

    public boolean isExpired(String jwt) {
        try {
            jwt = jwt.split(" ")[1].trim();
            return getExpirationDate(jwt).before(new Date());
        }
        catch(Exception e) {
            return true;
        }
    }

    public static Date getExpirationDate(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(authorizationHeader);
    }
}
