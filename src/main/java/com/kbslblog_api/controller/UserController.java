package com.kbslblog_api.controller;

import com.kbslblog_api.dto.common.ApiResult;
import com.kbslblog_api.dto.user.UserDto;
import com.kbslblog_api.dto.user.UserProfileUpdateDto;
import com.kbslblog_api.dto.user.UserRegisterDto;
import com.kbslblog_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;

    // @PostMapping(value = "/register")
    // public ResponseEntity<ApiResult> userRegister(@Valid @RequestBody UserRegisterDto userRegisterDto) {
    //     ApiResult result = new ApiResult();

    //     userService.registerUser(userRegisterDto);

    //     return ResponseEntity.ok().body(result);
    // }

    @GetMapping(value = "/me")
    public ResponseEntity<ApiResult> userGetMe() {
        ApiResult result = new ApiResult();

        UserDto userDto = userService.getUserMe();
        result.setData(userDto);

        return ResponseEntity.ok().body(result);
    }

    @PutMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult> userUpdateMe(@Valid @RequestBody UserProfileUpdateDto userProfileUpdateDto) throws Exception {
        ApiResult result = new ApiResult();

        userService.updateUserProfile(userProfileUpdateDto);

        return ResponseEntity.ok().body(result);
    }
}
