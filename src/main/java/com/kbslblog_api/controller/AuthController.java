package com.kbslblog_api.controller;

import com.kbslblog_api.dto.UserDto;
import com.kbslblog_api.model.User;
import com.kbslblog_api.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "signup"; // templates/signup.html
    }

    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute("userDto") UserDto userDto, BindingResult result) {
        if(result.hasErrors()){
            return "signup";
        }
        User existingUser = userService.findByUsername(userDto.getUsername());
        if(existingUser != null) {
            result.rejectValue("username", null, "이미 사용 중인 아이디입니다.");
            return "signup";
        }
        userService.save(userDto);
        return "redirect:/login?signupSuccess";
    }
}