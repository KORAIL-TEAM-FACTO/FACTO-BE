package team.java.facto_be.domain.user.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.global.security.jwt.types.Role;

import java.util.List;

public record UserInfoResponse(
        Long id,
        String email,
        String name,
        String lifeCycle,
        List<String> householdStatus,
        List<String> interestTheme,
        Integer age,
        String sidoName,
        String sigunguName,
        Role role
) {
    public static UserInfoResponse from(UserJpaEntity user) {
        ObjectMapper objectMapper = new ObjectMapper();

        List<String> householdStatusList;
        List<String> interestThemeList;

        try {
            householdStatusList = objectMapper.readValue(user.getHouseholdStatus(), new TypeReference<List<String>>() {});
            interestThemeList = objectMapper.readValue(user.getInterestTheme(), new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 오류", e);
        }

        return new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getLifeCycle(),
                householdStatusList,
                interestThemeList,
                user.getAge(),
                user.getSidoName(),
                user.getSigunguName(),
                user.getRole()
        );
    }
}
