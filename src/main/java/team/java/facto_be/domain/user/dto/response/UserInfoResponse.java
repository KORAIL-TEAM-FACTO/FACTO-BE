package team.java.facto_be.domain.user.dto.response;

import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.global.security.jwt.domain.entity.types.Role;

public record UserInfoResponse(
        Long id,
        String email,
        String name,
        String lifeCycleCode,
        String householdStatusCode,
        String interestThemeCode,
        Integer age,
        String sidoName,
        String sigunguName,
        Role role
) {
    public static UserInfoResponse from(UserJpaEntity user) {
        return new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getLifeCycleCode(),
                user.getHouseholdStatusCode(),
                user.getInterestThemeCode(),
                user.getAge(),
                user.getSidoName(),
                user.getSigunguName(),
                user.getRole()
        );
    }
}
