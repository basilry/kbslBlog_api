package com.kbslblog_api.repository;

import com.kbslblog_api.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {
    /**
 * Retrieves a Token entity matching the specified id, access token, and refresh token.
 *
 * @param id the unique identifier of the Token
 * @param accessToken the access token associated with the Token
 * @param refreshToken the refresh token associated with the Token
 * @return an Optional containing the matching Token if found, otherwise an empty Optional
 */
Optional<Token> findByIdAndAccessTokenAndRefreshToken(String id, String accessToken, String refreshToken);
}
