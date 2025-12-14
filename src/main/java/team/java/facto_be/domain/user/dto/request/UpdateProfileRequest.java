package team.java.facto_be.domain.user.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 마이페이지 내 정보 수정 요청 DTO.
 */
public record UpdateProfileRequest(
        @NotBlank
        @Size(min = 1, max = 30)
        String name,

        @NotBlank
        @Pattern(regexp = "영유아|아동|청소년|청년|중장년|노년|임신·출산")
        String lifeCycle,

        @NotBlank
        @Pattern(regexp = "다문화·탈북민|다자녀|보훈대상자|장애인|저소득|한부모·조손")
        String householdStatus,

        @NotBlank
        @Pattern(regexp = "신체건강|정신건강|생활지원|주거|일자리|문화·여가|안전·위기|임신·출산|보육|교육|입양·위탁|보호·돌봄|서민금융|법률")
        String interestTheme,

        @NotNull
        @Min(0)
        @Max(150)
        Integer age,

        @NotBlank
        @Size(max = 50)
        String sidoName,

        @NotBlank
        @Size(max = 50)
        String sigunguName
) {
}
