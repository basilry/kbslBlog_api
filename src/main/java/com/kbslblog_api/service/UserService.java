package com.kbslblog_api.service;

import com.kbslblog_api.constant.enums.ErrorCode;
import com.kbslblog_api.dto.user.UserRegisterDto;
import com.kbslblog_api.entity.User;
import com.kbslblog_api.exception.AlreadyExistException;
import com.kbslblog_api.mapper.UserMapper;
import com.kbslblog_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Registers a new user using the provided registration data.
     * <p>
     * This method checks for the uniqueness of the user's login ID, email, and phone number.
     * If any of these already exists in the system, it throws an AlreadyExistException with
     * the corresponding error code. Otherwise, the method encodes the user's password,
     * converts the registration data into a User entity, and saves it to the repository.
     *
     * @param userRegisterDto the registration data for the new user, including login ID, email, phone number, and password
     * @throws AlreadyExistException if the login ID, email, or phone number already exists
     */
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
}
