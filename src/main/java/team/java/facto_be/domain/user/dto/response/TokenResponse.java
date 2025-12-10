package team.java.facto_be.domain.user.dto.response;

/**
 * 액세스/리프레시 토큰 응답 DTO.
 */
public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
