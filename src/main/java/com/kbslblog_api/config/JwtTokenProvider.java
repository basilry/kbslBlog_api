package com.kbslblog_api.config;

import com.kbslblog_api.config.jwt.JwtUser;
import com.kbslblog_api.constant.Constants;
import com.kbslblog_api.constant.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
                            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds) {
        this.secret = secret;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        log.info("--------JwtTokenProvider createAccessToken authorities: {}", authorities);

        JwtUser user = (JwtUser) authentication.getPrincipal();

        log.info("--------JwtTokenProvider createAccessToken user: {}", user);

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(user.getLoginId())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(Constants.LOGIN_ID, user.getLoginId())
                .claim(Constants.ROLE, user.getRole().name())
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        log.info("--------JwtTokenProvider getAuthentication claims: {}", claims);

        if (claims.get(AUTHORITIES_KEY) == null || claims.get(Constants.LOGIN_ID) == null) {
            return null;
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(",")).map(SimpleGrantedAuthority::new).toList();

        log.info("--------JwtTokenProvider getAuthentication authorities: {}", authorities);

        String tokenId = this.getIdFromToken(token);
        String loginId = claims.get(Constants.LOGIN_ID).toString();
        UserRole role = UserRole.valueOf(claims.get(Constants.ROLE).toString());

        log.info("--------JwtTokenProvider getAuthentication tokenId: {}", tokenId);

        JwtUser principal = new JwtUser(tokenId, "", authorities, loginId, role);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public String getToken(String token) {
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        } else {
            return token;
        }
    }

    public String getIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(getToken(token))
                .getBody();

        return claims.getSubject();
    }

    public Map<String, Object> getDataFromToken(String token) {
        Map<String, Object> map = new HashMap<>();

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(getToken(token))
                .getBody();


        String loginId = claims.get(Constants.LOGIN_ID) == null ? null : claims.get(Constants.LOGIN_ID).toString();
        String role = claims.get(Constants.ROLE) == null ? null : claims.get(Constants.ROLE).toString();

        map.put(Constants.ID, claims.getSubject());
        map.put(Constants.LOGIN_ID, loginId);
        map.put(Constants.ROLE, role);

        return map;
    }

    public Map<String, Object> getDataFromRequest(HttpServletRequest request) {
        String token = request.getHeader(Constants.AUTHORIZATION);

        return getDataFromToken(token);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.info("JWT token is invalid");
        }

        return false;
    }
}
