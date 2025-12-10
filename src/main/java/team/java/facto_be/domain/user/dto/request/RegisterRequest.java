package team.java.facto_be.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 요청 DTO (필수 정보만 포함).
 */
public record RegisterRequest(
        @Email
        @NotBlank
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(min = 8, max = 60)
        String password,

        @NotBlank
        @Size(min = 1, max = 30)
        String name
) {
}
