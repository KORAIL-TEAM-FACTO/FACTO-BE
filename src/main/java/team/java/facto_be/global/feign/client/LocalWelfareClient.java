package team.java.facto_be.global.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import team.java.facto_be.global.feign.dto.LocalWelfareResponse;

/**
 * 한국사회보장정보원 지자체 복지서비스 조회 Feign 클라이언트.
 * 기본 URL: https://apis.data.go.kr/B554287/LocalGovernmentWelfareInformations
 */
@FeignClient(
        name = "localWelfareClient",
        url = "${local-welfare.base-url:https://apis.data.go.kr/B554287/LocalGovernmentWelfareInformations}"
)
public interface LocalWelfareClient {

    @GetMapping("/LcgvWelfarelist")
    LocalWelfareResponse getWelfareList(
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam(value = "pageNo", required = false) String pageNo,
            @RequestParam(value = "numOfRows", required = false) String numOfRows,
            @RequestParam(value = "lifeArray", required = false) String lifeArray,
            @RequestParam(value = "trgterIndvdlArray", required = false) String targetArray,
            @RequestParam(value = "intrsThemaArray", required = false) String interestArray,
            @RequestParam(value = "age", required = false) String age,
            @RequestParam(value = "ctpvNm", required = false) String ctpvNm,
            @RequestParam(value = "sggNm", required = false) String sggNm,
            @RequestParam(value = "srchKeyCode", required = false) String searchKeyCode,
            @RequestParam(value = "searchWrd", required = false) String searchWord,
            @RequestParam(value = "arrgOrd", required = false) String arrangeOrder
    );
}
