package team.java.facto_be.domain.chatbot.service.tool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import team.java.facto_be.domain.chatbot.service.context.UserContextHolder;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.domain.user.repository.UserRepository;
import team.java.facto_be.domain.welfare.entity.WelfareServiceJpaEntity;
import team.java.facto_be.domain.welfare.repository.WelfareServiceRepository;

import java.util.List;

/**
 * 로그인한 사용자의 프로필 정보를 기반으로 맞춤형 복지 서비스를 추천하는 Tool.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PersonalizedWelfareRecommendationTool {

    private final WelfareServiceRepository welfareServiceRepository;
    private final UserRepository userRepository;

    private static final int DEFAULT_LIMIT = 15;

    @Tool(description = """
            로그인한 사용자의 프로필 기반 맞춤형 복지 서비스 추천 도구.

            사용 시기:
            - 사용자가 1인칭("나", "내", "저", "제")을 사용하여 개인화된 추천을 요청할 때

            기능:
            - 로그인 사용자 프로필 자동 조회
            - 생애주기 / 지역 / 가구상태 기반 복지 서비스 추천
            - 파라미터 없이 자동 처리

            주의:
            - 사용자 정보를 추측하거나 임의로 생성하지 마세요
            """)
    public String recommendPersonalizedWelfare() {

        try {
            // 1️⃣ ThreadLocal에서 userId 조회
            Long userId = UserContextHolder.getUserId();

            if (userId == null) {
                log.warn("개인화 복지 추천 실패 - userId 없음");
                return "로그인 정보를 확인할 수 없습니다. 로그인 후 다시 시도해주세요.";
            }

            // 2️⃣ 사용자 조회
            UserJpaEntity user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new IllegalStateException("사용자를 찾을 수 없습니다. userId=" + userId));

            log.info("개인화 복지 추천 요청 - userId={}, lifeCycle={}, region={} {}",
                    userId,
                    user.getLifeCycle(),
                    user.getSidoName(),
                    user.getSigunguName()
            );

            // 3️⃣ 사용자 프로필 요약
            String profileSummary = buildProfileSummary(user);

            // ============================
            // 🔍 복지 서비스 검색 로직
            // ============================

            // 1단계: 생애주기 + 지역 + 가구상태 (가장 정밀)
            List<WelfareServiceJpaEntity> results =
                    welfareServiceRepository.searchWelfareServices(
                            user.getLifeCycle(),
                            user.getHouseholdStatus(),
                            user.getInterestTheme(),
                            user.getSidoName(),
                            user.getSigunguName(),
                            null,
                            DEFAULT_LIMIT
                    );

            // 2단계: 결과 부족 시 → 지역 + 생애주기
            if (results.size() < 5) {
                log.info("1단계 결과 부족 ({}건) → 지역 기반 fallback", results.size());

                results = welfareServiceRepository.searchByRegionAndCategory(
                        user.getSidoName(),
                        user.getLifeCycle(),
                        null,
                        DEFAULT_LIMIT
                );
            }

            // 3단계: 여전히 부족 시 → 키워드 + 지역
            if (results.size() < 3) {
                log.info("2단계 결과 부족 ({}건) → 키워드 기반 fallback", results.size());

                results = welfareServiceRepository.searchByKeywordWithRegion(
                        user.getSidoName(),
                        user.getSigunguName(),
                        user.getLifeCycle(),
                        DEFAULT_LIMIT
                );
            }

            // 결과 없음
            if (results.isEmpty()) {
                return profileSummary +
                        "========================================\n" +
                        "⚠️ 검색 결과: 0건\n" +
                        "========================================\n" +
                        "회원님의 조건에 맞는 복지 서비스를 찾지 못했습니다.\n" +
                        "지역이나 관심 조건을 변경해 다시 시도해보세요.\n";
            }

            // 결과 포맷팅
            return profileSummary + formatPersonalizedResults(results, user);

        } catch (IllegalStateException e) {
            log.error("개인화 복지 추천 실패 - 사용자 조회 실패", e);
            return "사용자 정보를 찾을 수 없습니다. 다시 로그인해주세요.";
        } catch (Exception e) {
            log.error("개인화 복지 추천 실패 - 예기치 못한 오류", e);
            return "복지 서비스 추천 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }

    // =====================================================
    // 📌 결과 포맷팅
    // =====================================================

    private String formatPersonalizedResults(
            List<WelfareServiceJpaEntity> results,
            UserJpaEntity user
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append("========================================\n");
        sb.append("✅ 맞춤형 복지 서비스 추천 결과\n");
        sb.append("========================================\n");
        sb.append(String.format("총 %d개의 복지 서비스를 찾았습니다.\n\n", results.size()));

        int displayCount = Math.min(results.size(), 10);

        for (int i = 0; i < displayCount; i++) {
            WelfareServiceJpaEntity w = results.get(i);

            sb.append(String.format("[%d] %s\n", i + 1, w.getServiceName()));

            sb.append("→ 추천 이유: ").append(buildReason(w, user)).append("\n");
            sb.append("→ 지역: ").append(composeRegion(w.getCtpvNm(), w.getSggNm())).append("\n");

            String summary = w.getAiSummary() != null
                    ? w.getAiSummary()
                    : w.getServiceSummary();

            if (summary != null) {
                sb.append("→ 내용: ").append(truncate(summary, 150)).append("\n");
            }

            if (w.getApplicationMethod() != null) {
                sb.append("→ 신청방법: ").append(w.getApplicationMethod()).append("\n");
            }

            if (w.getDetailLink() != null) {
                sb.append("→ 상세링크: ").append(truncate(w.getDetailLink(), 80)).append("\n");
            }

            sb.append("\n");
        }

        if (results.size() > displayCount) {
            sb.append(String.format("※ 이 외 %d개의 서비스가 더 있습니다.\n",
                    results.size() - displayCount));
        }

        return sb.toString();
    }

    // =====================================================
    // 📌 유틸 메서드
    // =====================================================

    private String buildProfileSummary(UserJpaEntity user) {
        StringBuilder sb = new StringBuilder();
        sb.append("【 ").append(user.getName()).append(" 님 프로필 】\n");
        sb.append("나이: ").append(user.getAge()).append("세");
        sb.append(" / 지역: ").append(user.getSidoName()).append(" ").append(user.getSigunguName());
        sb.append(" / 생애주기: ").append(user.getLifeCycle());

        if (user.getHouseholdStatus() != null && !user.getHouseholdStatus().isBlank()) {
            sb.append(" / 가구상태: ").append(user.getHouseholdStatus());
        }

        sb.append("\n\n");
        return sb.toString();
    }

    private String buildReason(WelfareServiceJpaEntity w, UserJpaEntity user) {
        if (w.getLifeCycleArray() != null &&
                w.getLifeCycleArray().contains(user.getLifeCycle())) {
            return "생애주기 일치";
        }
        if (isRegionMatch(w, user)) {
            return "거주 지역 대상";
        }
        return "관련 복지 서비스";
    }

    private boolean isRegionMatch(WelfareServiceJpaEntity w, UserJpaEntity user) {
        if (w.getCtpvNm() == null) return false;

        boolean sidoMatch =
                w.getCtpvNm().contains(user.getSidoName()) ||
                        user.getSidoName().contains(w.getCtpvNm());

        if (w.getSggNm() != null && user.getSigunguName() != null) {
            boolean sigunguMatch =
                    w.getSggNm().contains(user.getSigunguName()) ||
                            user.getSigunguName().contains(w.getSggNm());
            return sidoMatch && sigunguMatch;
        }

        return sidoMatch;
    }

    private String composeRegion(String ctpv, String sgg) {
        if (ctpv == null && sgg == null) return "-";
        if (sgg == null) return ctpv;
        if (ctpv == null) return sgg;
        return ctpv + " " + sgg;
    }

    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
}
