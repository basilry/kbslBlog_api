package com.kbslblog_api.config.jwt;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Objects;

@Getter
public class JwtUser extends User {

    private final Long id;

    public JwtUser(String userId, String password, Collection<? extends GrantedAuthority> authorities, Long id) {
        super(userId, password, authorities);
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof JwtUser that)) { return false; }
        if (!super.equals(o)) { return false; }

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}