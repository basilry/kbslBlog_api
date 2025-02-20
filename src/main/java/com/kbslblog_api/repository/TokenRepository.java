package com.kbslblog_api.repository;

import com.kbslblog_api.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {
    Optional<Token> findByIdAndAccessTokenAndRefreshToken(String id, String accessToken, String refreshToken);
}
