package team.java.facto_be.domain.welfare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.java.facto_be.domain.welfare.dto.response.RegionComparisonResponse;
import team.java.facto_be.domain.welfare.dto.response.WelfareServiceResponse;
import team.java.facto_be.domain.welfare.dto.response.WelfareServiceSummaryResponse;
import team.java.facto_be.domain.welfare.service.WelfareServiceService;

import java.util.List;

/**
 * 복지 서비스 REST API 컨트롤러.
 *
 * <p>복지 서비스 조회 API를 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/welfare-services")
public class WelfareServiceController {

    private final WelfareServiceService welfareServiceService;

    /**
     * 현재 로그인한 사용자 맞춤형 복지 서비스 목록을 조회합니다.
     *
     * <p>프론트엔드 호출 시점:
     * - 메인 페이지에서 맞춤형 복지 서비스 목록 표시
     *
     * <p>동작:
     * - 사용자의 프로필 정보(생애주기, 가구상태, 관심테마, 지역)를 기반으로 검색
     * - 조회수 많은 순으로 정렬
     * - 인증 필요
     *
     * @param limit 조회 개수 (기본값: 50, 최대: 100)
     * @return 맞춤형 복지 서비스 목록 (요약)
     */
    @GetMapping
    public List<WelfareServiceSummaryResponse> getRecommendedWelfareServices(
            @RequestParam(required = false) Integer limit
    ) {
        return welfareServiceService.getRecommendedWelfareServices(limit);
    }

    /**
     * 서비스 이름으로 복지 서비스를 검색합니다.
     *
     * <p>프론트엔드 호출 시점:
     * - 검색 페이지에서 사용자가 검색어 입력
     *
     * <p>동작:
     * - service_name에 키워드가 포함된 복지 서비스 검색
     * - 조회수 많은 순으로 정렬
     * - 인증 불필요
     *
     * @param keyword 검색 키워드
     * @param limit 조회 개수 (기본값: 50, 최대: 100)
     * @return 검색된 복지 서비스 목록 (요약)
     */
    @GetMapping("/search")
    public List<WelfareServiceSummaryResponse> searchWelfareServicesByName(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer limit
    ) {
        return welfareServiceService.searchByServiceName(keyword, limit);
    }

    /**
     * 두 지역 간 복지 서비스 개수를 비교합니다.
     *
     * <p>프론트엔드 호출 시점:
     * - 프로필 수정 페이지에서 지역 변경 시 미리보기
     *
     * <p>동작:
     * - 현재 로그인한 사용자의 기존 지역과 새로운 지역의 복지 서비스 개수 비교
     * - 생애주기를 기준으로 필터링된 개수 반환
     * - 인증 필요
     *
     * @param newSidoName 새로운 시도명
     * @param newSigunguName 새로운 시군구명
     * @return 지역별 복지 서비스 개수 비교 결과
     */
    @GetMapping("/region-comparison")
    public RegionComparisonResponse compareRegionWelfareCount(
            @RequestParam String newSidoName,
            @RequestParam String newSigunguName
    ) {
        return welfareServiceService.compareRegionWelfareCount(newSidoName, newSigunguName);
    }

    /**
     * 가장 최근 프로필 수정 시 변경된 지역의 복지 서비스 개수 차이를 조회합니다.
     *
     * <p>프론트엔드 호출 시점:
     * - 프로필 수정 완료 후 결과 화면에서 호출
     *
     * <p>동작:
     * - 가장 최근 프로필 변경 이력에서 이전 지역과 새 지역의 복지 서비스 개수 비교
     * - 지역 변경이 없었다면 차이가 0
     * - 인증 필요
     *
     * @return 최근 지역 변경에 따른 복지 서비스 개수 비교 결과
     */
    @GetMapping("/latest-region-change")
    public RegionComparisonResponse getLatestRegionChangeComparison() {
        return welfareServiceService.getLatestRegionChangeComparison();
    }

    /**
     * 복지 서비스 상세 정보를 조회합니다.
     *
     * <p>프론트엔드 호출 시점:
     * - 사용자가 복지 서비스 상세 페이지 진입
     *
     * <p>동작:
     * - 조회수 자동 증가
     * - 전체 상세 정보 반환
     * - 이 API 호출 후 POST /recent-views/{serviceId}도 함께 호출 필요
     * - 인증 불필요
     *
     * @param serviceId 복지 서비스 ID
     * @return 복지 서비스 상세 정보
     */
    @GetMapping("/{serviceId}")
    public WelfareServiceResponse getWelfareServiceDetail(@PathVariable String serviceId) {
        return welfareServiceService.getWelfareServiceDetail(serviceId);
    }
}
