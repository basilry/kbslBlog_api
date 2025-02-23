package com.kbslblog_api.config.jwt;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Objects;

@Getter
public class JwtUser extends User {

    private final Long id;

    /**
     * Constructs a new JwtUser with the specified credentials and authorities.
     *
     * <p>This constructor initializes the JwtUser by invoking the superclass constructor with the username,
     * password, and granted authorities, and then sets the unique identifier for the user.</p>
     *
     * @param userId the username associated with the user
     * @param password the user's password
     * @param authorities a collection of granted authorities assigned to the user
     * @param id the unique identifier for the JwtUser
     */
    public JwtUser(String userId, String password, Collection<? extends GrantedAuthority> authorities, Long id) {
        super(userId, password, authorities);
        this.id = id;
    }

    /**
     * Determines whether this JwtUser is equal to the specified object.
     *
     * <p>This method returns {@code true} if the given object is the same as this instance, or if it is an instance
     * of JwtUser with equivalent user details (as determined by the superclass) and an equal {@code id}.
     *
     * @param o the object to compare with this JwtUser
     * @return {@code true} if the specified object is equal to this JwtUser, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof JwtUser that)) { return false; }
        if (!super.equals(o)) { return false; }

        return Objects.equals(id, that.id);
    }

    /**
     * Computes and returns the hash code for this JWT user instance.
     *
     * <p>The hash code is based on the hash code of the superclass and the unique user ID,
     * ensuring consistency with the overridden equals method.</p>
     *
     * @return the computed hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}