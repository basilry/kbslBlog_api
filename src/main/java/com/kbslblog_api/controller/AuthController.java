package com.kbslblog_api.controller;

import com.kbslblog_api.config.JwtTokenProvider;
import com.kbslblog_api.dto.auth.LoginDto;
import com.kbslblog_api.dto.auth.TokenDto;
import com.kbslblog_api.dto.common.ApiResult;
import com.kbslblog_api.exception.UnAuthorizedException;
import com.kbslblog_api.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenService tokenService;

    /**
     * Authenticates a user with the provided login credentials and issues JWT tokens.
     * <p>
     * This method validates the user's login ID and password, and upon successful authentication,
     * generates an access token and a refresh token. The tokens are stored using the token service and
     * returned in an ApiResult wrapped in a ResponseEntity.
     * </p>
     *
     * @param loginDto the DTO containing the user's login ID and password
     * @return a ResponseEntity containing an ApiResult with a TokenDto that includes the generated access and refresh tokens
     * @throws UnAuthorizedException if authentication fails
     */
    @PostMapping("/authenticate")
    public ResponseEntity<ApiResult> authorize(@Valid @RequestBody LoginDto loginDto) {
        ApiResult result = new ApiResult();

        try {
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(loginDto.getLoginId(), loginDto.getPassword());

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtTokenProvider.createAccessToken(authentication);
            String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

            tokenService.saveToken(loginDto.getLoginId(), accessToken, refreshToken);

            TokenDto tokenDto = new TokenDto();
            tokenDto.setAccessToken(accessToken);
            tokenDto.setRefreshToken(refreshToken);

            result.setData(tokenDto);

            return ResponseEntity.ok().body(result);
        } catch (UnAuthorizedException e) {
            throw new UnAuthorizedException();
        }
    }

    /**
     * Refreshes the access token.
     *
     * <p>Validates the provided refresh token and, if valid, updates the access token. The new access token is 
     * returned wrapped in an ApiResult within a ResponseEntity. If the refresh token is invalid, an
     * UnAuthorizedException is thrown.
     *
     * @param tokenDto the tokens containing the current access and refresh tokens
     * @return a ResponseEntity with an ApiResult containing the updated access token
     * @throws UnAuthorizedException if the refresh token validation fails
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResult> refreshToken(@Valid @RequestBody TokenDto tokenDto) {
        ApiResult result = new ApiResult();

        if (!jwtTokenProvider.validateToken(tokenDto.getRefreshToken())) {
            throw new UnAuthorizedException();
        }

        TokenDto newAccessToken = tokenService.updateAccessToken(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
        result.setData(newAccessToken);

        return ResponseEntity.ok().body(result);
    }
}