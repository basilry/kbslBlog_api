package com.kbslblog_api.config;

import com.kbslblog_api.config.jwt.JwtUser;
import com.kbslblog_api.constant.Constants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private Key key;

    /**
     * Constructs a new JwtTokenProvider with the specified secret and token validity durations.
     *
     * <p>The secret (base64-encoded) is used for signing JWT tokens. The access and refresh token
     * validity periods are given in seconds and internally converted to milliseconds for expiration calculations.</p>
     *
     * @param secret the base64-encoded secret key for signing JWT tokens
     * @param accessTokenValidityInSeconds the validity period (in seconds) for the access token
     * @param refreshTokenValidityInSeconds the validity period (in seconds) for the refresh token
     */
    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
                            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds) {
        this.secret = secret;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
    }

    /**
     * Initializes the signing key for JWT generation.
     *
     * <p>This method decodes the Base64-encoded secret and generates the HMAC SHA key used for signing tokens.
     * It is automatically invoked by the Spring container after the bean properties are set.
     */
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT access token for the authenticated user.
     *
     * <p>This method creates an access token by extracting the user's authorities and unique identifier from the provided authentication object.
     * It includes these details as claims, signs the token using the configured key and HS512 algorithm, and sets the token's expiration based on the configured validity period.</p>
     *
     * @param authentication the authentication object containing user details and authorities
     * @return a compact JWT string representing the generated access token
     */
    public String createAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        JwtUser user = (JwtUser) authentication.getPrincipal();

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(Constants.ID, user.getId())
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    /**
     * Creates a refresh JWT token for the authenticated user.
     * <p>
     * The token's subject is set to the user's name (obtained from the provided authentication object)
     * and its expiration is set based on the configured refresh token validity period. It is signed using
     * the HS512 algorithm.
     * </p>
     *
     * @param authentication the authentication object containing the user's identity
     * @return a compact, signed JWT string representing the refresh token
     */
    public String createRefreshToken(Authentication authentication) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    /**
     * Extracts the actual token from a bearer token string.
     *
     * <p>If the provided token starts with the "Bearer " prefix, the method returns the token without the prefix;
     * otherwise, it returns the token unchanged.
     *
     * @param token the token string, potentially prefixed with "Bearer "
     * @return the token without the "Bearer " prefix if present, otherwise the original token
     */
    public String getToken(String token) {
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        } else {
            return token;
        }
    }

    /**
     * Extracts the user identifier from the provided JWT token.
     *
     * <p>This method parses the JWT token—removing any "Bearer " prefix if present—to retrieve its claims,
     * and then returns the subject claim, which represents the user ID.</p>
     *
     * @param token the JWT token, which may include a "Bearer " prefix
     * @return the user identifier extracted from the token's subject claim
     */
    public String getIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(getToken(token))
                .getBody();

        return claims.getSubject();
    }

    /**
     * Validates the provided JWT token.
     *
     * <p>This method attempts to parse the token using the signing key. If the parsing succeeds, the token is
     * considered valid and the method returns {@code true}. If parsing fails due to an invalid signature, malformed
     * token, expiration, unsupported format, or other issues, it logs the corresponding information and returns {@code false}.</p>
     *
     * @param token the JWT token to validate
     * @return {@code true} if the token is valid, {@code false} otherwise
     */
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
