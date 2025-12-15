package team.java.facto_be.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.java.facto_be.domain.user.dto.request.UpdateProfileRequest;
import team.java.facto_be.domain.user.dto.response.UserInfoResponse;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.domain.user.entity.UserProfileHistoryJpaEntity;
import team.java.facto_be.domain.user.facade.UserFacade;
import team.java.facto_be.domain.user.repository.UserProfileHistoryRepository;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserFacade userFacade;
    private final ObjectMapper objectMapper;
    private final UserProfileHistoryRepository userProfileHistoryRepository;

    @Transactional(readOnly = true)
    public UserInfoResponse getMyInfo() {
        UserJpaEntity user = userFacade.currentUser();
        return UserInfoResponse.from(user);
    }

    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
        UserJpaEntity user = userFacade.currentUser();

        try {
            String newHouseholdStatusJson = objectMapper.writeValueAsString(request.householdStatus());
            String newInterestThemeJson = objectMapper.writeValueAsString(request.interestTheme());

            // 프로필 변경 이력 저장 (변경 전 값을 기록)
            UserProfileHistoryJpaEntity history = UserProfileHistoryJpaEntity.builder()
                    .userId(user.getId())
                    .oldName(user.getName())
                    .newName(request.name())
                    .oldLifeCycle(user.getLifeCycle())
                    .newLifeCycle(request.lifeCycle())
                    .oldHouseholdStatus(user.getHouseholdStatus())
                    .newHouseholdStatus(newHouseholdStatusJson)
                    .oldInterestTheme(user.getInterestTheme())
                    .newInterestTheme(newInterestThemeJson)
                    .oldAge(user.getAge())
                    .newAge(request.age())
                    .oldSidoName(user.getSidoName())
                    .newSidoName(request.sidoName())
                    .oldSigunguName(user.getSigunguName())
                    .newSigunguName(request.sigunguName())
                    .build();

            userProfileHistoryRepository.save(history);

            // 프로필 업데이트
            user.updateProfile(
                    request.name(),
                    request.lifeCycle(),
                    newHouseholdStatusJson,
                    newInterestThemeJson,
                    request.age(),
                    request.sidoName(),
                    request.sigunguName()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }
    }
}
