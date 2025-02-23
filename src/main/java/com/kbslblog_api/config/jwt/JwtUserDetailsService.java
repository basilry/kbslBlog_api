package com.kbslblog_api.config.jwt;

import com.kbslblog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Component("userDetailsService")
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    /**
     * Loads the user's details by login identifier.
     *
     * <p>This method retrieves a user from the repository based on the provided login ID.
     * If a user is found, it converts the user entity into a UserDetails object for
     * authentication purposes. If no user is found, a UsernameNotFoundException is thrown.
     *
     * @param loginId the unique identifier for the user
     * @return the UserDetails corresponding to the found user
     * @throws UsernameNotFoundException if no user exists with the specified login ID
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String loginId) {
        return userRepository.findByLoginId(loginId)
                .map(this::createUser)
                .orElseThrow(() -> new UsernameNotFoundException(loginId + " NOT FOUND"));
    }

    /**
     * Converts a {@code User} entity into a {@code JwtUser} containing the user's authentication details.
     * <p>
     * This method extracts the user's role to create a single granted authority and uses it to instantiate
     * a new {@code JwtUser} with the user's login ID, password, and ID.
     *
     * @param user the user entity to be transformed into a Spring Security user details object
     * @return a {@code JwtUser} with the user's login ID, password, granted authority based on the user's role, and user ID
     */
    private org.springframework.security.core.userdetails.User createUser(com.kbslblog_api.entity.User user) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().name())
        );

        return new JwtUser(user.getLoginId(), user.getPassword(), authorities, user.getId());
    }
}