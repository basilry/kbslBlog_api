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