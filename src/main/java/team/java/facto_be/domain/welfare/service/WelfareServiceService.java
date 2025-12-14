package team.java.facto_be.domain.welfare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.java.facto_be.domain.welfare.dto.response.WelfareServiceResponse;
import team.java.facto_be.domain.welfare.entity.WelfareServiceJpaEntity;
import team.java.facto_be.domain.welfare.repository.WelfareServiceRepository;

/**
 * 복지 서비스 비즈니스 로직 서비스.
 *
 * <p>복지 서비스 상세 조회 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class WelfareServiceService {

    private final WelfareServiceRepository welfareServiceRepository;

    /**
     * 복지 서비스 상세 정보를 조회하고 조회수를 증가시킵니다.
     *
     * @param serviceId 복지 서비스 ID
     * @return 복지 서비스 상세 정보
     * @throws IllegalArgumentException 서비스를 찾을 수 없는 경우
     */
    @Transactional
    public WelfareServiceResponse getWelfareServiceDetail(String serviceId) {
        WelfareServiceJpaEntity service = welfareServiceRepository
                .findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("복지 서비스를 찾을 수 없습니다: " + serviceId));

        // 조회수 증가
        service.incrementInquiryCount();

        return WelfareServiceResponse.from(service);
    }
}
