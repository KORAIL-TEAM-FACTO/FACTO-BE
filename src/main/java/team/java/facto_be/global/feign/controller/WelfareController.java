package team.java.facto_be.global.feign.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.java.facto_be.global.feign.dto.LocalWelfareDetailResponse;
import team.java.facto_be.global.feign.service.WelfareService;

/**
 * 지자체 복지 서비스 API 컨트롤러.
 */
@Tag(name = "Welfare", description = "지자체 복지 서비스 API")
@RestController
@RequestMapping("/api/welfare")
@RequiredArgsConstructor
public class WelfareController {

    private final WelfareService welfareService;

    /**
     * 지자체 복지 서비스 상세 정보를 조회합니다.
     *
     * @param servId 서비스 ID
     * @return 지자체 복지 서비스 상세 정보
     */
    @Operation(summary = "복지 서비스 상세 조회", description = "서비스 ID로 지자체 복지 서비스의 상세 정보를 조회합니다.")
    @GetMapping("/detail")
    public ResponseEntity<LocalWelfareDetailResponse> getWelfareDetail(
            @Parameter(description = "서비스 ID", required = true)
            @RequestParam("servId") String servId
    ) {
        LocalWelfareDetailResponse response = welfareService.getWelfareDetail(servId);
        return ResponseEntity.ok(response);
    }
}
