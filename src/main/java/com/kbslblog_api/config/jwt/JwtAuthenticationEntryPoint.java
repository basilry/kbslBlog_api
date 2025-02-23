package com.kbslblog_api.config.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * Handles unauthorized access by sending a 401 (Unauthorized) error response.
     *
     * <p>This method is invoked when an authentication failure occurs. It responds with an HTTP 401 status
     * and a standard "Unauthorized" message.</p>
     *
     * @param request the HTTP request that resulted in the authentication error
     * @param response the HTTP response used to send the error message
     * @param authException the exception triggered by the authentication failure
     * @throws IOException if an I/O error occurs while sending the error response
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}