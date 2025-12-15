package team.java.facto_be.domain.chatbot.service.tool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.domain.user.facade.UserFacade;
import team.java.facto_be.domain.welfare.entity.WelfareServiceJpaEntity;
import team.java.facto_be.domain.welfare.repository.WelfareServiceRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 로그인한 사용자의 프로필 정보를 기반으로 맞춤형 복지 서비스를 추천하는 Tool.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PersonalizedWelfareRecommendationTool {

    private final WelfareServiceRepository welfareServiceRepository;
    private final UserFacade userFacade;
    private static final int DEFAULT_LIMIT = 15;

    @Tool(description = """
            ★★★ 반드시 이 도구를 사용해야 하는 경우 ★★★
            사용자가 다음과 같은 1인칭 표현을 사용하면 무조건 이 도구를 호출하세요:
            - "내가", "나는", "나의", "내", "나"
            - "저는", "저의", "제가", "제"
            - "우리", "우리가", "우리의"
            - "받을 수 있는", "해당되는", "적용되는"
            - "맞춤", "추천", "나한테", "저한테"

            예시:
            ✅ "내가 받을 수 있는 혜택"
            ✅ "나한테 맞는 복지"
            ✅ "저한테 추천해주세요"
            ✅ "내 조건에 맞는 지원금"
            ✅ "우리 가족이 받을 수 있는"
            ✅ "제가 신청 가능한"

            이 도구는:
            - 현재 로그인한 사용자의 실제 프로필을 자동으로 가져옵니다
            - 나이, 지역, 생애주기, 가구상태를 기반으로 검색합니다
            - 파라미터가 필요 없습니다 (자동 처리)

            ❌ 절대 사용자 정보를 추측하거나 가짜 데이터를 만들지 마세요!
            ❌ "홍길동" 같은 임의의 이름을 사용하지 마세요!
            """)
    public String recommendPersonalizedWelfare() {
        try {
            // 현재 로그인한 사용자 정보 조회
            UserJpaEntity user = userFacade.currentUser();

            log.info("개인화 복지 추천 - 사용자: {}, 생애주기: {}, 지역: {} {}",
                    user.getName(), user.getLifeCycle(), user.getSidoName(), user.getSigunguName());

            // 사용자 프로필 요약 (간소화)
            StringBuilder profileSummary = new StringBuilder();
            profileSummary.append("【 ").append(user.getName()).append(" 님 프로필 】\n");
            profileSummary.append("나이: ").append(user.getAge()).append("세");
            profileSummary.append(" / 지역: ").append(user.getSidoName()).append(" ").append(user.getSigunguName());
            profileSummary.append(" / 생애주기: ").append(user.getLifeCycle());

            if (user.getHouseholdStatus() != null && !user.getHouseholdStatus().isBlank()) {
                profileSummary.append(" / 가구상태: ").append(user.getHouseholdStatus());
            }
            profileSummary.append("\n\n");

            // 1단계: 생애주기 + 지역으로 검색
            // 빈 배열 문자열 "[]"을 null로 처리
            String householdStatus = isEmptyJsonArray(user.getHouseholdStatus()) ? null : user.getHouseholdStatus();
            String interestTheme = isEmptyJsonArray(user.getInterestTheme()) ? null : user.getInterestTheme();

            List<WelfareServiceJpaEntity> results = welfareServiceRepository.searchWelfareServices(
                    user.getLifeCycle(),
                    householdStatus,
                    interestTheme,
                    user.getSidoName(),
                    user.getSigunguName(),
                    null,  // 모든 서비스 타입
                    DEFAULT_LIMIT
            );

            // 2단계: 결과가 부족하면 지역만으로 재검색
            if (results.size() < 5) {
                log.info("결과 부족 ({}/{}), 지역 기반 검색으로 fallback", results.size(), DEFAULT_LIMIT);
                List<WelfareServiceJpaEntity> regionalResults = welfareServiceRepository.searchByRegionAndCategory(
                        user.getSidoName(),
                        user.getLifeCycle(),
                        null,
                        DEFAULT_LIMIT - results.size()
                );

                // 중복 제거하며 추가
                for (WelfareServiceJpaEntity service : regionalResults) {
                    if (results.stream().noneMatch(s -> s.getServiceId().equals(service.getServiceId()))) {
                        results.add(service);
                    }
                }
            }

            // 3단계: 여전히 부족하면 생애주기 키워드 + 지역 조건 유지하며 검색
            if (results.size() < 3) {
                log.info("여전히 결과 부족 ({}/{}), 지역 조건 유지하며 키워드 검색", results.size(), DEFAULT_LIMIT);
                List<WelfareServiceJpaEntity> keywordResults = welfareServiceRepository.searchByKeywordWithRegion(
                        user.getSidoName(),
                        user.getSigunguName(),
                        user.getLifeCycle(),
                        DEFAULT_LIMIT - results.size()
                );

                for (WelfareServiceJpaEntity service : keywordResults) {
                    if (results.stream().noneMatch(s -> s.getServiceId().equals(service.getServiceId()))) {
                        results.add(service);
                    }
                }
            }

            if (results.isEmpty()) {
                return profileSummary.toString() +
                       "죄송합니다. 현재 회원님의 조건에 맞는 복지 서비스를 찾지 못했습니다.\n" +
                       "지역을 변경하시거나 관심 분야를 조정해보시는 것은 어떨까요?";
            }

            // 결과 포맷팅
            return profileSummary.toString() + formatPersonalizedResults(results, user);

        } catch (Exception e) {
            log.error("개인화 복지 추천 실패", e);
            return "로그인 정보를 확인할 수 없습니다. 로그인 후 다시 시도해주세요.";
        }
    }

    /**
     * 개인화된 결과 포맷팅 (간소화)
     */
    private String formatPersonalizedResults(List<WelfareServiceJpaEntity> results, UserJpaEntity user) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("총 %d개의 맞춤형 복지 서비스를 찾았습니다.\n\n", results.size()));

        // 상위 10개만 출력 (너무 많으면 응답 잘림)
        int displayCount = Math.min(results.size(), 10);

        for (int i = 0; i < displayCount; i++) {
            WelfareServiceJpaEntity w = results.get(i);

            sb.append(String.format("[%d] %s\n", (i + 1), w.getServiceName()));

            // 추천 이유 (간단히)
            sb.append("→ 추천이유: ");
            List<String> reasons = new ArrayList<>();

            if (isRegionMatch(w, user)) {
                reasons.add("지역일치");
            }
            if (w.getLifeCycleArray() != null && w.getLifeCycleArray().contains(user.getLifeCycle())) {
                reasons.add("생애주기일치");
            }
            if (user.getHouseholdStatus() != null && !user.getHouseholdStatus().isBlank() &&
                w.getTargetArray() != null && w.getTargetArray().contains(user.getHouseholdStatus())) {
                reasons.add("대상일치");
            }

            if (reasons.isEmpty()) {
                sb.append("관련서비스");
            } else {
                sb.append(String.join(", ", reasons));
            }
            sb.append("\n");

            // 지역 정보
            sb.append("→ 지역: ").append(composeRegion(w.getCtpvNm(), w.getSggNm())).append("\n");

            // 요약 (짧게)
            String summary = w.getAiSummary() != null ? w.getAiSummary() : w.getServiceSummary();
            if (summary != null) {
                sb.append("→ 내용: ").append(truncate(summary, 150)).append("\n");
            }

            // 신청방법
            if (w.getApplicationMethod() != null) {
                sb.append("→ 신청: ").append(w.getApplicationMethod()).append("\n");
            }

            // 상세 링크
            if (w.getDetailLink() != null) {
                sb.append("→ 상세: ").append(truncate(w.getDetailLink(), 80)).append("\n");
            }

            sb.append("\n");
        }

        if (results.size() > displayCount) {
            sb.append(String.format("※ 이 외 %d개의 서비스가 더 있습니다.\n", results.size() - displayCount));
        }

        return sb.toString();
    }

    private boolean isRegionMatch(WelfareServiceJpaEntity w, UserJpaEntity user) {
        if (w.getCtpvNm() == null) return false;

        boolean sidoMatch = w.getCtpvNm().contains(user.getSidoName()) ||
                           user.getSidoName().contains(w.getCtpvNm());

        if (w.getSggNm() != null && user.getSigunguName() != null) {
            boolean sigunguMatch = w.getSggNm().contains(user.getSigunguName()) ||
                                  user.getSigunguName().contains(w.getSggNm());
            return sidoMatch && sigunguMatch;
        }

        return sidoMatch;
    }

    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    private String nullToDash(Object obj) {
        return obj == null ? "-" : obj.toString();
    }

    private String composeRegion(String ctpv, String sgg) {
        if (ctpv == null && sgg == null) return "-";
        if (sgg == null) return ctpv;
        if (ctpv == null) return sgg;
        return ctpv + " " + sgg;
    }

    /**
     * JSON 배열 문자열이 빈 배열인지 확인
     * "[]", null, "" 모두 true 반환
     */
    private boolean isEmptyJsonArray(String jsonArray) {
        if (jsonArray == null || jsonArray.isBlank()) {
            return true;
        }
        String trimmed = jsonArray.trim();
        return trimmed.equals("[]") || trimmed.equals("[ ]");
    }
}
