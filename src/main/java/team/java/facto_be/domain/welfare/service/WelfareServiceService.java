package team.java.facto_be.domain.welfare.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.domain.user.facade.UserFacade;
import team.java.facto_be.domain.welfare.dto.response.WelfareServiceResponse;
import team.java.facto_be.domain.welfare.dto.response.WelfareServiceSummaryResponse;
import team.java.facto_be.domain.welfare.entity.WelfareServiceJpaEntity;
import team.java.facto_be.domain.welfare.repository.WelfareServiceRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 복지 서비스 비즈니스 로직 서비스.
 *
 * <p>복지 서비스 상세 조회 및 맞춤형 목록 조회 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class WelfareServiceService {

    private static final int DEFAULT_LIMIT = 50;
    private static final int MAX_LIMIT = 100;

    private final WelfareServiceRepository welfareServiceRepository;
    private final UserFacade userFacade;
    private final ObjectMapper objectMapper;

    /**
     * 서비스 이름으로 복지 서비스를 검색합니다.
     *
     * @param keyword 검색 키워드
     * @param limit 조회 개수 (선택, 기본값: 50, 최대: 100)
     * @return 검색된 복지 서비스 목록 (요약)
     */
    @Transactional(readOnly = true)
    public List<WelfareServiceSummaryResponse> searchByServiceName(String keyword, Integer limit) {
        // limit 검증
        if (limit == null || limit <= 0 || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }

        // 키워드로 검색
        List<WelfareServiceJpaEntity> results = welfareServiceRepository.searchByKeyword(keyword, limit);

        return results.stream()
                .map(WelfareServiceSummaryResponse::from)
                .toList();
    }

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

    /**
     * 현재 로그인한 사용자의 프로필 정보를 기반으로 맞춤형 복지 서비스 목록을 조회합니다.
     *
     * @param limit 조회 개수 (선택, 기본값: 50, 최대: 100)
     * @return 맞춤형 복지 서비스 목록 (요약)
     */
    @Transactional(readOnly = true)
    public List<WelfareServiceSummaryResponse> getRecommendedWelfareServices(Integer limit) {
        // 현재 사용자 정보 가져오기
        UserJpaEntity user = userFacade.currentUser();

        // limit 검증
        if (limit == null || limit <= 0 || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }

        // JSON 배열 파싱
        List<String> householdStatusList;
        List<String> interestThemeList;

        try {
            householdStatusList = objectMapper.readValue(user.getHouseholdStatus(), new TypeReference<List<String>>() {});
            interestThemeList = objectMapper.readValue(user.getInterestTheme(), new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("사용자 프로필 정보 파싱 오류", e);
        }

        // 복지 서비스 검색 (여러 조건으로 검색하여 결합)
        List<WelfareServiceJpaEntity> results = new ArrayList<>();

        // 빈 리스트 처리: 최소 1개 항목 보장 (null로 대체)
        if (householdStatusList.isEmpty()) {
            householdStatusList.add(null);
        }
        if (interestThemeList.isEmpty()) {
            interestThemeList.add(null);
        }

        // 1. 생애주기 + 각 가구상태 + 각 관심테마로 검색
        for (String householdStatus : householdStatusList) {
            for (String interestTheme : interestThemeList) {
                List<WelfareServiceJpaEntity> services = welfareServiceRepository.searchWelfareServices(
                        user.getLifeCycle(),
                        householdStatus,
                        interestTheme,
                        user.getSidoName(),
                        user.getSigunguName(),
                        null,
                        limit
                );

                // 중복 제거하며 추가
                for (WelfareServiceJpaEntity service : services) {
                    if (!results.contains(service)) {
                        results.add(service);
                    }
                }

                if (results.size() >= limit) {
                    break;
                }
            }
            if (results.size() >= limit) {
                break;
            }
        }

        // limit만큼만 반환
        return results.stream()
                .limit(limit)
                .map(WelfareServiceSummaryResponse::from)
                .toList();
    }
}
