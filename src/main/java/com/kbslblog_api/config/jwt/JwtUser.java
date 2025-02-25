package com.kbslblog_api.config.jwt;

import com.kbslblog_api.constant.enums.UserRole;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@Getter
public class JwtUser extends User {

    private final String loginId;
    private final UserRole role;

    public JwtUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String loginId, UserRole role) {
        super(username, password, authorities);
        this.loginId = loginId;
        this.role = role;

        log.info("------------JwtUser: {}", this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof JwtUser that)) { return false; }
        if (!super.equals(o)) { return false; }

        return Objects.equals(loginId, that.loginId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), loginId);
    }
}