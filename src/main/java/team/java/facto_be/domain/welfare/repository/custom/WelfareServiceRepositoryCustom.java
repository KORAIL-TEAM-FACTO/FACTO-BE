package team.java.facto_be.domain.welfare.repository.custom;

import team.java.facto_be.domain.welfare.entity.WelfareServiceJpaEntity;

import java.util.List;

/**
 * 복지 서비스 커스텀 Repository 인터페이스.
 *
 * <p>QueryDSL을 사용한 복잡한 검색 쿼리를 정의합니다.
 */
public interface WelfareServiceRepositoryCustom {

    /**
     * 복지 서비스를 검색합니다.
     *
     * <p>생애주기, 대상, 관심 테마, 지역 등으로 필터링하여
     * 사용자에게 맞는 복지 서비스를 찾습니다.
     *
     * @param lifeCycleCode 생애주기 코드 (nullable)
     * @param householdStatusCode 가구상태 코드 (nullable)
     * @param interestThemeCode 관심 테마 코드 (nullable)
     * @param sidoName 시도명 (nullable)
     * @param sigunguName 시군구명 (nullable)
     * @param serviceType 서비스 타입 (nullable)
     * @param limit 조회 개수
     * @return 검색된 복지 서비스 목록
     */
    List<WelfareServiceJpaEntity> searchWelfareServices(
            String lifeCycleCode,
            String householdStatusCode,
            String interestThemeCode,
            String sidoName,
            String sigunguName,
            String serviceType,
            int limit
    );

    /**
     * 키워드로 복지 서비스를 검색합니다.
     *
     * @param keyword 검색 키워드
     * @param limit 조회 개수
     * @return 검색된 복지 서비스 목록
     */
    List<WelfareServiceJpaEntity> searchByKeyword(String keyword, int limit);
}
