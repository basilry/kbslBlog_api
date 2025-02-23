package com.kbslblog_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "token")
@NoArgsConstructor
@Getter
public class Token {

    @Id
    @Nationalized
    private String id;

    @Nationalized
    private String accessToken;

    @Nationalized
    private String refreshToken;

    /**
     * Updates the access token for this token instance.
     *
     * @param accessToken the new access token value to set
     */
    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Updates the refresh token of this Token instance.
     *
     * @param refreshToken the new refresh token value
     */
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * Constructs a new Token instance with the specified identifier, access token, and refresh token.
     *
     * <p>This constructor is utilized by Lombok's @Builder to create a token entity with
     * the necessary authentication tokens.
     *
     * @param id the unique identifier for the token
     * @param accessToken the access token used for authenticating access
     * @param refreshToken the refresh token used to obtain a new access token
     */
    @Builder
    public Token(String id, String accessToken, String refreshToken) {
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
