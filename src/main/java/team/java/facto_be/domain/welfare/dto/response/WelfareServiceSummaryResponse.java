package team.java.facto_be.domain.welfare.dto.response;

import team.java.facto_be.domain.welfare.entity.WelfareServiceJpaEntity;

/**
 * 복지 서비스 요약 응답 DTO.
 *
 * <p>복지 서비스 목록 조회 시 사용하는 간단한 정보만 포함합니다.
 */
public record WelfareServiceSummaryResponse(
        String serviceId,
        String serviceName,
        String aiSummary,
        String ctpvNm,
        String sggNm,
        String supportType,
        String serviceType,
        Integer inquiryCount
) {
    public static WelfareServiceSummaryResponse from(WelfareServiceJpaEntity entity) {
        return new WelfareServiceSummaryResponse(
                entity.getServiceId(),
                entity.getServiceName(),
                entity.getAiSummary(),
                entity.getCtpvNm(),
                entity.getSggNm(),
                entity.getSupportType(),
                entity.getServiceType(),
                entity.getInquiryCount()
        );
    }
}
