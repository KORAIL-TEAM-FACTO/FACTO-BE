package team.java.facto_be.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.java.facto_be.domain.user.dto.request.RegisterRequest;
import team.java.facto_be.domain.user.dto.request.UpdateProfileRequest;
import team.java.facto_be.domain.user.dto.request.UserLoginRequest;
import team.java.facto_be.domain.user.dto.response.TokenResponse;
import team.java.facto_be.domain.user.service.UserLoginService;
import team.java.facto_be.domain.user.service.UserProfileService;
import team.java.facto_be.domain.user.service.UserRegisterService;

/**
 * 사용자 회원가입/로그인 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserLoginService userLoginService;
    private final UserRegisterService userRegisterService;
    private final UserProfileService userProfileService;

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody UserLoginRequest request) {
        return userLoginService.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        userRegisterService.register(request);
    }

    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        userProfileService.updateProfile(request);
    }
}
