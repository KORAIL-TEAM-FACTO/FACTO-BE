package team.java.facto_be.domain.recentview.dto.response;

/**
 * 인기 복지 서비스 응답 DTO.
 *
 * <p>QueryDSL Projection으로 직접 생성되므로
 * from() 메서드 없이 생성자만 사용합니다.
 */
public record TrendingWelfareResponse(
        String welfareServiceId,
        Long viewCount
) {
}
