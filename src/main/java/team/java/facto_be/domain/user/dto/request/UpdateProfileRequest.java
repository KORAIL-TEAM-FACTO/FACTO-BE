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
        @Pattern(regexp = "001|002|003|004|005|006|007")
        String lifeCycleCode,

        @NotBlank
        @Pattern(regexp = "010|020|030|040|050|060")
        String householdStatusCode,

        @NotBlank
        @Pattern(regexp = "010|020|030|040|050|060|070|080|090|100|110|120|130|140")
        String interestThemeCode,

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
