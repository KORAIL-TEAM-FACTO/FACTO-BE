package team.java.facto_be.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.java.facto_be.global.security.jwt.JwtTokenProvider;
import team.java.facto_be.global.security.jwt.types.Role;
import team.java.facto_be.domain.user.dto.request.UserLoginRequest;
import team.java.facto_be.domain.user.dto.response.TokenResponse;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.domain.user.repository.UserRepository;

/**
 * 로그인 서비스: 사용자 검증 후 토큰 발급.
 */
@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public TokenResponse login(UserLoginRequest request) {
        UserJpaEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return jwtTokenProvider.generateToken(user.getEmail(), Role.USER.toString());
    }
}
