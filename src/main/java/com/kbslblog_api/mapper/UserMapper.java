package com.kbslblog_api.mapper;

import com.kbslblog_api.dto.user.UserDto;
import com.kbslblog_api.dto.user.UserRegisterDto;
import com.kbslblog_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
 * Converts a User entity to its corresponding UserDto.
 *
 * @param entity the User entity to convert; may be null
 * @return the corresponding UserDto, or null if the input is null
 */
UserDto toUserDto(User entity);

    /**
 * Converts a UserRegisterDto into a User entity.
 *
 * @param entity the user registration data transfer object containing registration details
 * @return a User entity with the corresponding information from the registration DTO
 */
User toUser(UserRegisterDto entity);
}
