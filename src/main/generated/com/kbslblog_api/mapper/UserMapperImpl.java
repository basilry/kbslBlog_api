package com.kbslblog_api.mapper;

import com.kbslblog_api.constant.enums.UserRole;
import com.kbslblog_api.dto.user.UserDto;
import com.kbslblog_api.dto.user.UserRegisterDto;
import com.kbslblog_api.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-14T15:06:58+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto getUserMapper(User entity) {
        if ( entity == null ) {
            return null;
        }

        String loginId = null;
        String name = null;
        String email = null;
        String phoneNumber = null;
        String role = null;
        String description = null;
        String profileImg = null;

        loginId = entity.getLoginId();
        name = entity.getName();
        email = entity.getEmail();
        phoneNumber = entity.getPhoneNumber();
        if ( entity.getRole() != null ) {
            role = entity.getRole().name();
        }
        description = entity.getDescription();
        profileImg = entity.getProfileImg();

        UserDto userDto = new UserDto( loginId, name, email, phoneNumber, role, description, profileImg );

        return userDto;
    }

    @Override
    public User saveUserMapper(UserRegisterDto entity) {
        if ( entity == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.loginId( entity.getLoginId() );
        user.password( entity.getPassword() );
        user.name( entity.getName() );
        user.email( entity.getEmail() );
        user.phoneNumber( entity.getPhoneNumber() );
        if ( entity.getRole() != null ) {
            user.role( Enum.valueOf( UserRole.class, entity.getRole() ) );
        }
        user.description( entity.getDescription() );

        return user.build();
    }
}
