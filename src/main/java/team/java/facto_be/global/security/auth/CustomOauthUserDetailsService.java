package team.java.facto_be.global.security.auth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.java.facto_be.global.security.jwt.domain.entity.types.Role;

/**
 * OAuth2 사용자 정보를 간단히 AuthDetails로 변환합니다.
 * (추가 사용자 생성 로직 없이 기본 USER 역할로 처리)
 */
@Service
public class CustomOauthUserDetailsService extends DefaultOAuth2UserService {

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Object emailAttr = oAuth2User.getAttributes().getOrDefault("email", userRequest.getClientRegistration().getRegistrationId());
        String email = emailAttr != null ? emailAttr.toString() : userRequest.getClientRegistration().getRegistrationId();
        return new AuthDetails(email, Role.USER.name(), oAuth2User.getAttributes());
    }
}
