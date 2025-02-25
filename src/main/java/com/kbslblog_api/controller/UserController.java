package com.kbslblog_api.controller;

import com.kbslblog_api.dto.common.ApiResult;
import com.kbslblog_api.dto.user.UserDto;
import com.kbslblog_api.dto.user.UserRegisterDto;
import com.kbslblog_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;

    // 만들어야 할 api 목록
    // 1. 회원가입
    // 3. 회원정보 수정
    // 4. 회원탈퇴
    // 5. 회원목록 조회
    // 6. 회원 상세 조회

    @PostMapping(value = "/register")
    public ResponseEntity<ApiResult> userRegister(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        ApiResult result = new ApiResult();

        userService.registerUser(userRegisterDto);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/me")
    public ResponseEntity<ApiResult> userGetMe() {
        ApiResult result = new ApiResult();

        UserDto userDto = userService.getUserMe();
        result.setData(userDto);

        return ResponseEntity.ok().body(result);
    }
}
