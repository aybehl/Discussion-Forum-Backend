package com.forum.discussion_platform.controller;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.AuthRequestDTO;
import com.forum.discussion_platform.dto.response.AuthResponseDTO;
import com.forum.discussion_platform.dto.response.SuccessResponseDTO;
import com.forum.discussion_platform.enums.ApiStatus;
import com.forum.discussion_platform.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponseDTO<AuthResponseDTO>> signUp(@RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authService.signUp(request);
        return new ResponseEntity<>(new SuccessResponseDTO<>(
                ApiStatus.SUCCESS,
                response,
                HttpStatus.CREATED,
                GenericConstants.USER_REGISTRATION_SUCCESSFUL),
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponseDTO<AuthResponseDTO>> login(@RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return new ResponseEntity<>(new SuccessResponseDTO<>(
                ApiStatus.SUCCESS,
                response,
                HttpStatus.OK,
                GenericConstants.USER_LOGIN_SUCCESSFUL),
                HttpStatus.OK);
    }



}
