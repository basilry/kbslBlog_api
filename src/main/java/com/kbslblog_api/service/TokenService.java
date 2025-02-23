package com.kbslblog_api.service;

import com.kbslblog_api.config.JwtTokenProvider;
import com.kbslblog_api.config.jwt.JwtUser;
import com.kbslblog_api.config.jwt.JwtUserDetailsService;
import com.kbslblog_api.dto.auth.TokenDto;
import com.kbslblog_api.entity.Token;
import com.kbslblog_api.exception.UnAuthorizedException;
import com.kbslblog_api.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtUserDetailsService jwtUserDetailsService;

    /**
     * Saves a token entity using the specified token identifier.
     * <p>
     * This method retrieves an existing token from the repository using the given ID. If a token is found,
     * its current access and refresh tokens are preserved; otherwise, a new token with null token values is created.
     * The resulting token entity is then saved to the repository.
     *
     * @param id the unique identifier of the token
     * @param accessToken the access token (unused in this implementation)
     * @param refreshToken the refresh token (unused in this implementation)
     */
    public void saveToken(String id, String accessToken, String refreshToken) {
        Token token = tokenRepository.findById(id).orElse(null);

        Token.TokenBuilder tokenBuilder = Token.builder().id(id);

        tokenBuilder
                .accessToken(token == null ? null : token.getAccessToken())
                .refreshToken(token == null ? null : token.getRefreshToken());

        tokenRepository.save(tokenBuilder.build());
    }

    /**
     * Updates the access token associated with a token record using a provided refresh token.
     * <p>
     * This method extracts the user ID from the refresh token, retrieves the token record matching
     * the given access token and refresh token, and validates its existence. It then loads the user's
     * details, generates a new access token based on the user's authorities, updates the token record,
     * and returns a TokenDto containing the newly created access token.
     * </p>
     *
     * @param accessToken the current access token associated with the token record
     * @param refreshToken the refresh token from which the user ID is extracted
     * @return a TokenDto containing the new access token
     * @throws UnAuthorizedException if the token record is not found or does not match the provided tokens
     */
    public TokenDto updateAccessToken(String accessToken, String refreshToken) {
        String id = jwtTokenProvider.getIdFromToken(refreshToken);

        Token token = tokenRepository.findByIdAndAccessTokenAndRefreshToken(id, accessToken, refreshToken).orElseThrow(UnAuthorizedException::new);

        JwtUser user = (JwtUser) jwtUserDetailsService.loadUserByUsername(id);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        JwtUser principal = new JwtUser(id, "", authorities, user.getId());

        String newAccessToken = jwtTokenProvider.createAccessToken(new UsernamePasswordAuthenticationToken(principal, null, authorities));

        token.updateAccessToken(newAccessToken);


        TokenDto tokenDto = new TokenDto();
        tokenDto.setAccessToken(newAccessToken);

        return tokenDto;
    }
}
