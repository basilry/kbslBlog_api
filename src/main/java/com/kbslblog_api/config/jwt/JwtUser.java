package com.kbslblog_api.config.jwt;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Objects;

@Getter
public class JwtUser extends User {

    private final Long userId;

    public JwtUser(String id, String password, Collection<? extends GrantedAuthority> authorities, Long userId) {
        super(id, password, authorities);
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof JwtUser that)) { return false; }
        if (!super.equals(o)) { return false; }

        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId);
    }
}