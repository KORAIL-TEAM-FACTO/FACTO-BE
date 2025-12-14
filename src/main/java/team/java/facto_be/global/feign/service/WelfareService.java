package team.java.facto_be.global.feign.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.java.facto_be.global.feign.client.LocalWelfareClient;
import team.java.facto_be.global.feign.dto.LocalWelfareDetailResponse;

/**
 * 지자체 복지 서비스 관련 비즈니스 로직을 처리하는 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WelfareService {

    private final LocalWelfareClient localWelfareClient;

    @Value("${welfare.api.service-key}")
    private String serviceKey;

    /**
     * 지자체 복지 서비스 상세 정보를 조회합니다.
     *
     * @param servId 서비스 ID
     * @return 지자체 복지 서비스 상세 정보
     */
    public LocalWelfareDetailResponse getWelfareDetail(String servId) {
        return localWelfareClient.getWelfareDetail(serviceKey, servId);
    }
}
