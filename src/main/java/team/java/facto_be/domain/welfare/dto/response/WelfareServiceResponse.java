package team.java.facto_be.domain.welfare.dto.response;

import team.java.facto_be.domain.welfare.entity.WelfareServiceJpaEntity;

/**
 * 복지 서비스 응답 DTO.
 *
 * <p>복지 서비스의 전체 정보를 반환합니다.
 */
public record WelfareServiceResponse(
        String serviceId,
        String serviceName,
        String serviceSummary,
        String aiSummary,
        String ctpvNm,
        String sggNm,
        String bizChrDeptNm,
        String supportType,
        String supportCycle,
        String applicationMethod,
        String lifeCycleArray,
        String targetArray,
        String interestThemeArray,
        String supportTargetContent,
        String selectionCriteria,
        String serviceContent,
        String applicationMethodContent,
        Integer inquiryCount,
        String detailLink,
        String lastModifiedDate,
        String serviceType,
        String serviceUrl,
        String site,
        String contact,
        String department,
        String organization,
        Integer baseYear,
        String organizationName,
        String projectStartDate,
        String projectEndDate,
        String requiredDocuments,
        String etc,
        String householdStatus
) {
    public static WelfareServiceResponse from(WelfareServiceJpaEntity entity) {
        return new WelfareServiceResponse(
                entity.getServiceId(),
                entity.getServiceName(),
                entity.getServiceSummary(),
                entity.getAiSummary(),
                entity.getCtpvNm(),
                entity.getSggNm(),
                entity.getBizChrDeptNm(),
                entity.getSupportType(),
                entity.getSupportCycle(),
                entity.getApplicationMethod(),
                entity.getLifeCycleArray(),
                entity.getTargetArray(),
                entity.getInterestThemeArray(),
                entity.getSupportTargetContent(),
                entity.getSelectionCriteria(),
                entity.getServiceContent(),
                entity.getApplicationMethodContent(),
                entity.getInquiryCount(),
                entity.getDetailLink(),
                entity.getLastModifiedDate(),
                entity.getServiceType(),
                entity.getServiceUrl(),
                entity.getSite(),
                entity.getContact(),
                entity.getDepartment(),
                entity.getOrganization(),
                entity.getBaseYear(),
                entity.getOrganizationName(),
                entity.getProjectStartDate(),
                entity.getProjectEndDate(),
                entity.getRequiredDocuments(),
                entity.getEtc(),
                entity.getHouseholdStatus()
        );
    }
}
