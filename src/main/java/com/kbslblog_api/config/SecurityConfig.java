package com.kbslblog_api.config;

import com.kbslblog_api.config.jwt.JwtAuthenticationEntryPoint;
import com.kbslblog_api.config.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${env.security.allowed_origin}")
    private String allowedOrigins;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Returns a BCryptPasswordEncoder instance for encoding passwords.
     *
     * <p>This bean is used to securely hash and verify user credentials within the application.</p>
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures and builds the security filter chain for the application.
     * <p>
     * This method disables CSRF protection, sets up CORS with dynamic allowed origins (as specified in the application properties) and permits all methods and headers with credentials. It also defines stateless session management, sets public access for endpoints such as "/users/register", "/authenticate", "/refresh", and "/healthCheck", and requires authentication for all other requests. Additionally, it configures exception handling using a JWT authentication entry point and adds a JWT filter to process authentication tokens.
     * </p>
     *
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during security configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {
                    CorsConfigurationSource source = request -> {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowCredentials(true);
                        config.addAllowedMethod("*");
                        config.addAllowedHeader("*");

                        for (String val : allowedOrigins.split(","))  {
                            config.addAllowedOrigin(val);
                        }

                        return config;
                    };

                    cors.configurationSource(source);
                })
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/register").permitAll()
                        .requestMatchers("/authenticate").permitAll()
                        .requestMatchers("/refresh").permitAll()
                        .requestMatchers("/healthCheck").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}