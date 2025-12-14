package team.java.facto_be.domain.welfare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.java.facto_be.domain.welfare.dto.response.WelfareServiceResponse;
import team.java.facto_be.domain.welfare.service.WelfareServiceService;

/**
 * 복지 서비스 REST API 컨트롤러.
 *
 * <p>복지 서비스 상세 조회 API를 제공합니다.
 * 인증이 필요하지 않습니다 (공개 API).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/welfare-services")
public class WelfareServiceController {

    private final WelfareServiceService welfareServiceService;

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
     *
     * @param serviceId 복지 서비스 ID
     * @return 복지 서비스 상세 정보
     */
    @GetMapping("/{serviceId}")
    public WelfareServiceResponse getWelfareServiceDetail(@PathVariable String serviceId) {
        return welfareServiceService.getWelfareServiceDetail(serviceId);
    }
}
