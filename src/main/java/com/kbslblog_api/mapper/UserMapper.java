package com.kbslblog_api.mapper;

import com.kbslblog_api.dto.user.UserDto;
import com.kbslblog_api.dto.user.UserRegisterDto;
import com.kbslblog_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toUserDto(User entity);

    User toUser(UserRegisterDto entity);
}
