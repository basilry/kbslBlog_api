package com.kbslblog_api.config.jwt;

import com.kbslblog_api.config.JwtTokenProvider;
import com.kbslblog_api.constant.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Processes an HTTP request by attempting to extract a JWT token from the Authorization header
     * and then delegating the request to the next filter in the chain.
     * <p>
     * The method casts the incoming request to {@code HttpServletRequest} to retrieve the JWT using
     * {@code resolveToken}. While there is commented-out code for validating the token and setting up
     * authentication, the request is currently always passed along the filter chain without applying
     * any authentication logic.
     * </p>
     *
     * @param servletRequest the incoming request containing potential JWT information
     * @param servletResponse the response associated with the request
     * @param filterChain the filter chain to which the request and response are delegated
     * @throws IOException if an I/O error occurs during request processing
     * @throws ServletException if a servlet error occurs during request processing
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

//        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
//            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
//
//            if (authentication != null) {
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//                log.debug("Save authentication : '{}', uri: {}", authentication.getName(), requestURI);
//            } else {
//                log.debug("No authentication, uri: {}", requestURI);
//            }
//        } else {
//            log.debug("No JWT token, uri: {}", requestURI);
//        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * Extracts the JWT token from the "Authorization" header of the provided HTTP request.
     *
     * <p>This method checks if the "Authorization" header is present and begins with the "Bearer " prefix.
     * If so, it returns the token without the prefix; otherwise, it returns {@code null}.
     *
     * @param request the HTTP request containing the potential JWT token
     * @return the JWT token without the "Bearer " prefix, or {@code null} if not present or invalid
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(Constants.AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}