package com.forum.discussion_platform.service;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.AuthRequestDTO;
import com.forum.discussion_platform.dto.response.AuthResponseDTO;
import com.forum.discussion_platform.enums.UserRole;
import com.forum.discussion_platform.exception.ResourceNotFoundException;
import com.forum.discussion_platform.model.User;
import com.forum.discussion_platform.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final GoogleAuthService googleAuthService;

    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, GoogleAuthService googleAuthService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.googleAuthService = googleAuthService;
        this.tokenService = tokenService;
    }

    public AuthResponseDTO signUp(AuthRequestDTO request) {
        if (request.getGoogleToken() != null) {
            // Google sign-up
//            GoogleUser googleUser = googleAuthService.verifyToken(request.getGoogleToken());
//            User user = userRepository.findByEmail(googleUser.getEmail())
//                    .orElseGet(() -> createUserFromGoogle(googleUser));
//            String jwt = tokenService.generateToken(user);
//            return new AuthResponseDTO("Google sign-up successful", jwt);
            return null;
        } else {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException(GenericConstants.EMAIL_ALREADY_IN_USE);
            }

            // Hash the password using BCrypt
            String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

            User newUser = User.builder()
                    .email(request.getEmail())
                    .passwordHash(hashedPassword)
                    .role(request.getRole())
                    .build();
            userRepository.save(newUser);

            String jwt = tokenService.generateToken(newUser.getUserId(), UserRole.USER);
            return new AuthResponseDTO(jwt, newUser.getUserId());
        }
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        if (request.getGoogleToken() != null) {
            // Google login
//            GoogleUser googleUser = googleAuthService.verifyToken(request.getGoogleToken());
//            User user = userRepository.findByEmail(googleUser.getEmail())
//                    .orElseGet(() -> createUserFromGoogle(googleUser));
//            String jwt = tokenService.generateToken(user);
//            return new AuthResponseDTO("Google sign-up successful", jwt);
            return null;
        } else {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.USER_NOT_FOUND_BY_EMAIL));

            if(!BCrypt.checkpw(request.getPassword(), user.getPasswordHash())){
                throw new IllegalArgumentException(GenericConstants.INVALID_PASSWORD);
            }

            String jwt = tokenService.generateToken(user.getUserId(), request.getRole());

            return new AuthResponseDTO(jwt, user.getUserId());
        }
    }


//    private User createUserFromGoogle(GoogleUser googleUser) {
//        User user = new User();
//        user.setEmail(googleUser.getEmail());
//        user.setUsername(googleUser.getName());
//        return userRepository.save(user);
//    }
}
