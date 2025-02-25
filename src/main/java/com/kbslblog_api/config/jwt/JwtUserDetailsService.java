package com.kbslblog_api.config.jwt;

import com.kbslblog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Component("userDetailsService")
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String loginId) {

        log.info("--------JwtUserDetailsService loadUserByUsername loginId: {}", loginId);

        return userRepository.findByLoginId(loginId)
                .map(this::createUser)
                .orElseThrow(() -> new UsernameNotFoundException(loginId + " NOT FOUND"));
    }

    private org.springframework.security.core.userdetails.User createUser(com.kbslblog_api.entity.User user) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().name())
        );

        return new JwtUser(user.getLoginId(), user.getPassword(), authorities, user.getLoginId(), user.getRole());
    }
}