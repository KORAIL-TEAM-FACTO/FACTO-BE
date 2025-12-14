package team.java.facto_be.domain.user.service;

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

    @Transactional(readOnly = true)
    public UserInfoResponse getMyInfo() {
        UserJpaEntity user = userFacade.currentUser();
        return UserInfoResponse.from(user);
    }

    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
        UserJpaEntity user = userFacade.currentUser();

        user.updateProfile(
                request.name(),
                request.lifeCycle(),
                request.householdStatus(),
                request.interestTheme(),
                request.age(),
                request.sidoName(),
                request.sigunguName()
        );
    }
}
