package com.kbslblog_api.service;

import com.kbslblog_api.config.JwtTokenProvider;
import com.kbslblog_api.constant.Constants;
import com.kbslblog_api.constant.enums.ErrorCode;
import com.kbslblog_api.dto.user.UserDto;
import com.kbslblog_api.dto.user.UserRegisterDto;
import com.kbslblog_api.entity.User;
import com.kbslblog_api.exception.AlreadyExistException;
import com.kbslblog_api.exception.NotFoundException;
import com.kbslblog_api.mapper.UserMapper;
import com.kbslblog_api.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final HttpServletRequest request;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public void registerUser(UserRegisterDto userRegisterDto) {
        User user = userRepository.findByLoginId(userRegisterDto.getLoginId()).stream().findFirst().orElse(null);

        if(user != null) {
            if(userRepository.existsByLoginId(userRegisterDto.getLoginId())) {
                throw new AlreadyExistException(ErrorCode.ID_ALREADY_EXISTS);
            } else if(userRepository.existsByEmail(userRegisterDto.getEmail())) {
                throw new AlreadyExistException(ErrorCode.EMAIL_ALREADY_EXISTS);
            } else if(userRepository.existsByPhoneNumber(userRegisterDto.getPhoneNumber())) {
                throw new AlreadyExistException(ErrorCode.PHONE_NUMBER_ALREADY_EXISTS);
            }
        }

        userRegisterDto.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        User newUser = userMapper.toUser(userRegisterDto);

        userRepository.save(newUser);
    }

    public UserDto getUserMe() {
        Map<String, Object> map = jwtTokenProvider.getDataFromRequest(request);

        log.info("map: {}", map);

        String tokenLoginId = (String) map.get(Constants.LOGIN_ID);

        User user = userRepository.findByLoginId(tokenLoginId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserDto(user);
    }
}
