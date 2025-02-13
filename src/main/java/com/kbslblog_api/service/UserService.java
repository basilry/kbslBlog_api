package com.kbslblog_api.service;

import com.kbslblog_api.dto.UserDto;
import com.kbslblog_api.model.User;

public interface UserService {
    User findByUsername(String username);
    User save(UserDto userDto);
}