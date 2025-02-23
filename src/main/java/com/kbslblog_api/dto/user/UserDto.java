package com.kbslblog_api.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String loginId;
    private String name;
    private String email;
    private String phoneNumber;
    private String role;
    private String description;

    /**
     * Constructs a new UserDto with the specified user details.
     *
     * @param loginId     the user's login identifier
     * @param name        the user's name
     * @param email       the user's email address
     * @param phoneNumber the user's phone number
     * @param role        the user's role
     * @param description a description of the user
     */
    public UserDto(String loginId, String name, String email, String phoneNumber, String role, String description) {
        this.loginId = loginId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.description = description;
    }
}
