package team.java.facto_be.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.java.facto_be.domain.user.dto.request.UpdateProfileRequest;
import team.java.facto_be.domain.user.dto.response.UserInfoResponse;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.domain.user.facade.UserFacade;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserFacade userFacade;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public UserInfoResponse getMyInfo() {
        UserJpaEntity user = userFacade.currentUser();
        return UserInfoResponse.from(user);
    }

    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
        UserJpaEntity user = userFacade.currentUser();

        try {
            String householdStatusJson = objectMapper.writeValueAsString(request.householdStatus());
            String interestThemeJson = objectMapper.writeValueAsString(request.interestTheme());

            user.updateProfile(
                    request.name(),
                    request.lifeCycle(),
                    householdStatusJson,
                    interestThemeJson,
                    request.age(),
                    request.sidoName(),
                    request.sigunguName()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }
    }
}
