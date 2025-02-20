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

import java.util.Collections;
import java.util.stream.Collectors;

@Component("userDetailsService")
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String id) {

        return userRepository.findByUsername(id)
                .map(this::createUser)
                .orElseThrow(() -> new UsernameNotFoundException(id + " NOT FOUND"));
    }

    private org.springframework.security.core.userdetails.User createUser(com.kbslblog_api.entity.User user) {
        Collections<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));

        return new JwtUser(user.getId(), user.getPassword(), authorities, user.getUserId());
    }
}