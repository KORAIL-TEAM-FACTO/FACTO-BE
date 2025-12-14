package team.java.facto_be.domain.welfare.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import team.java.facto_be.domain.welfare.entity.WelfareServiceJpaEntity;

import java.util.List;

import static team.java.facto_be.domain.welfare.entity.QWelfareServiceJpaEntity.welfareServiceJpaEntity;

/**
 * 복지 서비스 커스텀 Repository 구현체.
 *
 * <p>QueryDSL을 사용하여 복잡한 검색 로직을 구현합니다.
 */
@RequiredArgsConstructor
public class WelfareServiceRepositoryImpl implements WelfareServiceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<WelfareServiceJpaEntity> searchWelfareServices(
            String lifeCycleCode,
            String householdStatusCode,
            String interestThemeCode,
            String sidoName,
            String sigunguName,
            String serviceType,
            int limit
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        // 생애주기 필터 (JSON 배열에 포함 여부)
        if (lifeCycleCode != null && !lifeCycleCode.isEmpty()) {
            builder.and(welfareServiceJpaEntity.lifeCycleArray.contains(lifeCycleCode));
        }

        // 대상 필터 (JSON 배열에 포함 여부)
        if (householdStatusCode != null && !householdStatusCode.isEmpty()) {
            builder.and(welfareServiceJpaEntity.targetArray.contains(householdStatusCode));
        }

        // 관심 테마 필터 (JSON 배열에 포함 여부)
        if (interestThemeCode != null && !interestThemeCode.isEmpty()) {
            builder.and(welfareServiceJpaEntity.interestThemeArray.contains(interestThemeCode));
        }

        // 시도 필터 (부분 일치로 변경 - "대전" <-> "대전광역시" 매칭)
        if (sidoName != null && !sidoName.isEmpty()) {
            builder.and(welfareServiceJpaEntity.ctpvNm.contains(sidoName));
        }

        // 시군구 필터 (부분 일치)
        if (sigunguName != null && !sigunguName.isEmpty()) {
            builder.and(welfareServiceJpaEntity.sggNm.contains(sigunguName));
        }

        // 서비스 타입 필터 (CENTRAL, LOCAL, PRIVATE)
        if (serviceType != null && !serviceType.isEmpty()) {
            builder.and(welfareServiceJpaEntity.serviceType.eq(serviceType));
        }

        return queryFactory
                .selectFrom(welfareServiceJpaEntity)
                .where(builder)
                .orderBy(welfareServiceJpaEntity.inquiryCount.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<WelfareServiceJpaEntity> searchByKeyword(String keyword, int limit) {
        return queryFactory
                .selectFrom(welfareServiceJpaEntity)
                .where(
                        welfareServiceJpaEntity.serviceName.contains(keyword)
                                .or(welfareServiceJpaEntity.serviceSummary.contains(keyword))
                                .or(welfareServiceJpaEntity.aiSummary.contains(keyword))
                                .or(welfareServiceJpaEntity.serviceContent.contains(keyword))
                )
                .orderBy(welfareServiceJpaEntity.inquiryCount.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<WelfareServiceJpaEntity> searchByRegionAndCategory(
            String region,
            String category,
            String serviceType,
            int limit
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        // 지역 필터 (부분 일치)
        if (region != null && !region.isEmpty()) {
            builder.and(
                    welfareServiceJpaEntity.ctpvNm.contains(region)
                            .or(welfareServiceJpaEntity.sggNm.contains(region))
            );
        }

        // 카테고리 필터 (서비스명, 요약, 내용에서 검색)
        if (category != null && !category.isEmpty()) {
            builder.and(
                    welfareServiceJpaEntity.serviceName.contains(category)
                            .or(welfareServiceJpaEntity.serviceSummary.contains(category))
                            .or(welfareServiceJpaEntity.aiSummary.contains(category))
                            .or(welfareServiceJpaEntity.serviceContent.contains(category))
                            .or(welfareServiceJpaEntity.lifeCycleArray.contains(category))
                            .or(welfareServiceJpaEntity.targetArray.contains(category))
                            .or(welfareServiceJpaEntity.interestThemeArray.contains(category))
            );
        }

        // 서비스 타입 필터
        if (serviceType != null && !serviceType.isEmpty()) {
            builder.and(welfareServiceJpaEntity.serviceType.eq(serviceType));
        }

        return queryFactory
                .selectFrom(welfareServiceJpaEntity)
                .where(builder)
                .orderBy(welfareServiceJpaEntity.inquiryCount.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<WelfareServiceJpaEntity> searchByKeywordWithRegion(
            String sidoName,
            String sigunguName,
            String keyword,
            int limit
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        // 키워드 필터 (필수)
        if (keyword != null && !keyword.isEmpty()) {
            builder.and(
                    welfareServiceJpaEntity.serviceName.contains(keyword)
                            .or(welfareServiceJpaEntity.serviceSummary.contains(keyword))
                            .or(welfareServiceJpaEntity.aiSummary.contains(keyword))
                            .or(welfareServiceJpaEntity.serviceContent.contains(keyword))
            );
        }

        // 지역 필터 (부분 일치) - 시도 또는 시군구가 있으면 해당 지역으로 제한
        if (sidoName != null && !sidoName.isEmpty()) {
            builder.and(welfareServiceJpaEntity.ctpvNm.contains(sidoName));
        }

        if (sigunguName != null && !sigunguName.isEmpty()) {
            builder.and(welfareServiceJpaEntity.sggNm.contains(sigunguName));
        }

        return queryFactory
                .selectFrom(welfareServiceJpaEntity)
                .where(builder)
                .orderBy(welfareServiceJpaEntity.inquiryCount.desc())
                .limit(limit)
                .fetch();
    }
}
