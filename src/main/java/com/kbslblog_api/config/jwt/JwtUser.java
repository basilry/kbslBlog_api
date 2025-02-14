package com.kbslblog_api.config.jwt;


import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Objects;

@Getter
public class JwtUser extends User {

    private final Long memberId;
    private final String companyCode;

    public JwtUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String companyCode, Long memberId) {
        super(username, password, authorities);
        this.companyCode = companyCode;
        this.memberId = memberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof JwtUser that)) { return false; }
        if (!super.equals(o)) { return false; }

        return Objects.equals(companyCode, that.companyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), companyCode);
    }
}