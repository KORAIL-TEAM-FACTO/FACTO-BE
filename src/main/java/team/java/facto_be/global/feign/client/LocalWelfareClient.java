package team.java.facto_be.global.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import team.java.facto_be.global.feign.dto.LocalWelfareDetailResponse;

/**
 * 지자체 복지 서비스 API FeignClient.
 *
 * <p>공공데이터포털의 지자체 복지 서비스 정보 API를 호출합니다.
 */
@FeignClient(
        name = "localWelfareClient",
        url = "https://apis.data.go.kr/B554287/LocalGovernmentWelfareInformations"
)
public interface LocalWelfareClient {

    /**
     * 지자체 복지 서비스 상세 정보를 조회합니다.
     *
     * @param serviceKey 인증키
     * @param servId 서비스 ID
     * @return 지자체 복지 서비스 상세 정보
     */
    @GetMapping("/LcgvWelfaredetailed")
    LocalWelfareDetailResponse getWelfareDetail(
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam("servId") String servId
    );
}
