package team.java.facto_be.domain.welfare.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import team.java.facto_be.global.entity.BaseTimeEntity;

/**
 * 복지 서비스 JPA 엔티티.
 *
 * <p>공공 데이터 포털의 복지 서비스 정보를 저장합니다.
 * mobok 데이터베이스의 welfare_services 테이블과 매핑됩니다.
 *
 * <p>서비스 타입:
 * - CENTRAL: 중앙 정부 복지 서비스
 * - LOCAL: 지자체 복지 서비스
 * - PRIVATE: 민간 복지 서비스
 */
@Entity(name = "WelfareServiceJpaEntity")
@Table(name = "welfare_services")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WelfareServiceJpaEntity extends BaseTimeEntity {

    /**
     * 복지 서비스 고유 ID
     */
    @Id
    @Column(name = "service_id", length = 50)
    private String serviceId;

    /**
     * 복지 서비스 이름
     */
    @Column(name = "service_name", length = 500, nullable = false)
    private String serviceName;

    /**
     * 서비스 요약 (원본)
     */
    @Column(name = "service_summary", columnDefinition = "TEXT")
    private String serviceSummary;

    /**
     * AI 생성 요약
     */
    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    /**
     * 시도명 (서울특별시, 경기도 등)
     */
    @Column(name = "ctpv_nm", length = 100)
    private String ctpvNm;

    /**
     * 시군구명 (강남구, 수원시 등)
     */
    @Column(name = "sgg_nm", length = 100)
    private String sggNm;

    /**
     * 업무 담당 부서명
     */
    @Column(name = "biz_chr_dept_nm", length = 200)
    private String bizChrDeptNm;

    /**
     * 지원 유형 (현금, 현물, 서비스 등)
     */
    @Column(name = "support_type", length = 100)
    private String supportType;

    /**
     * 지원 주기 (월별, 분기별, 연간 등)
     */
    @Column(name = "support_cycle", length = 100)
    private String supportCycle;

    /**
     * 신청 방법 (온라인, 방문, 우편 등)
     */
    @Column(name = "application_method", length = 100)
    private String applicationMethod;

    /**
     * 생애주기 배열 (JSON 형식)
     * 예: ["001", "004", "005"]
     * 001: 영유아, 002: 아동, 003: 청소년, 004: 청년, 005: 중장년, 006: 노년, 007: 임신·출산
     */
    @Column(name = "life_cycle_array", columnDefinition = "TEXT")
    private String lifeCycleArray;

    /**
     * 대상 배열 (JSON 형식)
     * 예: ["010", "020", "050"]
     * 010: 다문화·탈북민, 020: 다자녀, 030: 보훈대상자, 040: 장애인, 050: 저소득, 060: 한부모·조손
     */
    @Column(name = "target_array", columnDefinition = "TEXT")
    private String targetArray;

    /**
     * 관심 테마 배열 (JSON 형식)
     * 예: ["010", "050", "100"]
     * 010: 신체건강, 020: 정신건강, 030: 생활지원, 040: 주거, 050: 일자리,
     * 060: 문화·여가, 070: 안전·위기, 080: 임신·출산, 090: 보육, 100: 교육,
     * 110: 입양·위탁, 120: 보호·돌봄, 130: 서민금융, 140: 법률
     */
    @Column(name = "interest_theme_array", columnDefinition = "TEXT")
    private String interestThemeArray;

    /**
     * 지원 대상 내용 (상세 설명)
     */
    @Column(name = "support_target_content", columnDefinition = "TEXT")
    private String supportTargetContent;

    /**
     * 선정 기준
     */
    @Column(name = "selection_criteria", columnDefinition = "TEXT")
    private String selectionCriteria;

    /**
     * 서비스 내용 (상세 설명)
     */
    @Column(name = "service_content", columnDefinition = "TEXT")
    private String serviceContent;

    /**
     * 신청 방법 내용 (상세 설명)
     */
    @Column(name = "application_method_content", columnDefinition = "TEXT")
    private String applicationMethodContent;

    /**
     * 조회 수
     */
    @Builder.Default
    @Column(name = "inquiry_count", nullable = false)
    private Integer inquiryCount = 0;

    /**
     * 상세 정보 링크
     */
    @Column(name = "detail_link", columnDefinition = "TEXT")
    private String detailLink;

    /**
     * 마지막 수정 날짜
     */
    @Column(name = "last_modified_date", length = 20)
    private String lastModifiedDate;

    /**
     * 서비스 타입 (CENTRAL, LOCAL, ( ?? ) )
     */
    @Builder.Default
    @Column(name = "service_type", length = 20, nullable = false)
    private String serviceType = "LOCAL";

    /**
     * 서비스 URL
     */
    @Column(name = "service_url", columnDefinition = "TEXT")
    private String serviceUrl;

    /**
     * 사이트 정보
     */
    @Column(name = "site", columnDefinition = "TEXT")
    private String site;

    /**
     * 담당자 연락처
     */
    @Column(name = "contact", length = 200)
    private String contact;

    /**
     * 부서명
     */
    @Column(name = "department", length = 200)
    private String department;

    /**
     * 기관명
     */
    @Column(name = "organization", length = 200)
    private String organization;

    /**
     * 기준 연도
     */
    @Column(name = "base_year")
    private Integer baseYear;

    /**
     * 운영 기관명
     */
    @Column(name = "organization_name", length = 200)
    private String organizationName;

    /**
     * 사업 시작 일자
     */
    @Column(name = "project_start_date", length = 20)
    private String projectStartDate;

    /**
     * 사업 종료 일자
     */
    @Column(name = "project_end_date", length = 20)
    private String projectEndDate;

    /**
     * 제출 서류
     */
    @Column(name = "required_documents", columnDefinition = "TEXT")
    private String requiredDocuments;

    /**
     * 기타 정보
     */
    @Column(name = "etc", columnDefinition = "TEXT")
    private String etc;

    /**
     * 가구 상태 정보 (JSON 형식)
     */
    @Column(name = "household_status", columnDefinition = "TEXT")
    private String householdStatus;

    /**
     * 조회수 증가
     */
    public void incrementInquiryCount() {
        this.inquiryCount++;
    }
}
