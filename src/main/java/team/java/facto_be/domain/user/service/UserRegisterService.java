package team.java.facto_be.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.java.facto_be.global.security.jwt.types.Role;
import team.java.facto_be.domain.user.dto.request.RegisterRequest;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.domain.user.repository.UserRepository;

/**
 * 회원가입 서비스: 신규 사용자 저장.
 */
@Service
@RequiredArgsConstructor
public class UserRegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterRequest request){

        if(userRepository.existsByEmail(request.email())){
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        UserJpaEntity user = UserJpaEntity.builder()
                .email(request.email())
                .password(encodedPassword)
                .name(request.name())
                .lifeCycle(request.lifeCycle())
                .householdStatus(request.householdStatus())
                .interestTheme(request.interestTheme())
                .age(request.age())
                .sidoName(request.sidoName())
                .sigunguName(request.sigunguName())
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }
}
