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

    public void saveToken(String id, String accessToken, String refreshToken) {
        Token token = tokenRepository.findById(id).orElse(null);

        Token.TokenBuilder tokenBuilder = Token.builder().id(id);

        tokenBuilder
                .accessToken(token == null ? null : token.getAccessToken())
                .refreshToken(token == null ? null : token.getRefreshToken());

        tokenRepository.save(tokenBuilder.build());
    }

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
