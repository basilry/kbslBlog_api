package com.kbslblog_api.controller;

import com.kbslblog_api.dto.common.ApiResult;
import com.kbslblog_api.dto.user.UserRegisterDto;
import com.kbslblog_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "users")
public class UserController {
    private final UserService userService;

    // 만들어야 할 api 목록
    // 1. 회원가입
    // 3. 회원정보 수정
    // 4. 회원탈퇴
    // 5. 회원목록 조회
    /**
     * Registers a new user.
     *
     * <p>
     * Processes a user registration request by validating the provided registration data and delegating
     * the registration logic to the user service. Returns a standard API result wrapped in a ResponseEntity
     * with an HTTP 200 OK status.
     * </p>
     *
     * @param userRegisterDto the registration details for the new user
     * @return a ResponseEntity containing an ApiResult representing the outcome of the registration
     */

    @PostMapping(value = "/register")
    public ResponseEntity<ApiResult> userRegister(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        ApiResult result = new ApiResult();

        userService.registerUser(userRegisterDto);

        return ResponseEntity.ok().body(result);
    }
}
