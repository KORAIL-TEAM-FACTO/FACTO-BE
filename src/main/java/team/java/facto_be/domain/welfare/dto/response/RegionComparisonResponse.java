package team.java.facto_be.domain.welfare.dto.response;

/**
 * 지역별 복지 서비스 개수 비교 응답 DTO.
 *
 * <p>프로필 수정 시 지역 변경에 따른 복지 서비스 개수 차이를 보여줍니다.
 */
public record RegionComparisonResponse(
        String currentSidoName,
        String currentSigunguName,
        Integer currentCount,
        String newSidoName,
        String newSigunguName,
        Integer newCount,
        Integer difference
) {
    public static RegionComparisonResponse of(
            String currentSidoName,
            String currentSigunguName,
            Integer currentCount,
            String newSidoName,
            String newSigunguName,
            Integer newCount
    ) {
        return new RegionComparisonResponse(
                currentSidoName,
                currentSigunguName,
                currentCount,
                newSidoName,
                newSigunguName,
                newCount,
                newCount - currentCount
        );
    }
}
