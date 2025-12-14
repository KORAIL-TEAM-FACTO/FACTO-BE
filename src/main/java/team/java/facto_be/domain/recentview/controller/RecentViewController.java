package team.java.facto_be.domain.recentview.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.java.facto_be.domain.recentview.dto.response.RecentViewResponse;
import team.java.facto_be.domain.recentview.dto.response.TrendingWelfareResponse;
import team.java.facto_be.domain.recentview.service.RecentViewService;

import java.util.List;

/**
 * 최근 본 복지 서비스 REST API 컨트롤러.
 *
 * <p>최근 본 복지 서비스 추가, 조회, 인기 복지 서비스 TOP 10 API를 제공합니다.
 * addRecentView, getMyRecentViews는 JWT 인증이 필요하지만,
 * getTrendingWelfareServices는 인증 없이 조회 가능합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/recent-views")
public class RecentViewController {

    private final RecentViewService recentViewService;

    /**
     * 복지 서비스를 최근 본 목록에 추가합니다.
     *
     * <p>프론트엔드 호출 시점:
     * 사용자가 복지 서비스 상세 페이지에 진입할 때 자동으로 호출
     * (상세 페이지 컴포넌트의 useEffect 또는 componentDidMount 등에서)
     *
     * <p>동작:
     * - 같은 복지 서비스를 다시 보면 중복 저장하지 않고 조회 시간만 업데이트
     * - 사용자의 최근 본 기록이 100개 초과하면 가장 오래된 것 자동 삭제
     * - 이 데이터는 인기 복지 서비스 TOP 10 집계에도 사용됨
     *
     * @param welfareServiceId 복지 서비스 ID
     * @return 201 Created (바디 없음)
     */
    @PostMapping("/{welfareServiceId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addRecentView(@PathVariable String welfareServiceId) {
        recentViewService.addRecentView(welfareServiceId);
    }

    /**
     * 현재 사용자의 최근 본 복지 서비스 목록을 조회합니다.
     *
     * <p>프론트엔드 호출 시점: 사용자가 "최근 본 복지 서비스" 페이지 진입
     *
     * @param limit 조회 개수 (기본값: 100, 최대: 100)
     * @return 최근 본 복지 서비스 목록 (최신순)
     */
    @GetMapping
    public List<RecentViewResponse> getMyRecentViews(
            @RequestParam(required = false, defaultValue = "100") int limit
    ) {
        return recentViewService.getMyRecentViews(limit);
    }

    /**
     * 전체 사용자가 최근에 가장 많이 본 복지 서비스 TOP N을 조회합니다.
     *
     * <p>프론트엔드 호출 시점:
     * - 메인 페이지의 "인기 복지 서비스" 섹션
     * - 복지 서비스 추천 위젯 등
     *
     * <p>인증 불필요: 로그인하지 않은 사용자도 조회 가능
     *
     * <p>집계 방식:
     * - 최근 N일간 각 복지 서비스를 본 고유 사용자 수를 집계
     * - 같은 사용자가 여러 번 본 것은 1번으로 카운트 (중복 제거)
     * - 조회수 많은 순으로 정렬
     *
     * @param days 집계 기간 (기본값: 7일)
     * @param limit 조회 개수 (기본값: 10, 최대: 100)
     * @return 인기 복지 서비스 TOP N (조회수 많은 순)
     */
    @GetMapping("/trending")
    public List<TrendingWelfareResponse> getTrendingWelfareServices(
            @RequestParam(required = false, defaultValue = "7") int days,
            @RequestParam(required = false, defaultValue = "10") int limit
    ) {
        return recentViewService.getTrendingWelfareServices(days, limit);
    }
}
