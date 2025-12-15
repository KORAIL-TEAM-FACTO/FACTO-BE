package team.java.facto_be.domain.chatbot.service.tool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import team.java.facto_be.domain.welfare.entity.WelfareServiceJpaEntity;
import team.java.facto_be.domain.welfare.repository.WelfareServiceRepository;

import java.util.List;

/**
 * AI Tool Calling에 사용되는 복지 서비스 검색 기능
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WelfareSearchTool {

    private final WelfareServiceRepository welfareServiceRepository;
    private static final int DEFAULT_LIMIT = 10;

    @Tool(description = """
            일반적인 복지 서비스 검색 도구입니다. 지역과 카테고리로 넓게 검색합니다.
            사용자가 지역이나 대상/주제를 언급하면 이 도구를 우선 사용하세요.
            예: '대전 청소년 복지', '서울 노인 지원', '경기 주거 혜택'

            ⚠️ 중요: 사용자가 지역을 언급했다면 반드시 region 파라미터를 입력하세요!
            예: "대전에 있는 혜택" → region="대전" (필수!)
            """)
    public String searchWelfare(
            @ToolParam(description = "지역명 (선택). 예: 대전, 서울, 경기, 부산 등. 없으면 전국 검색") String region,
            @ToolParam(description = "카테고리/대상/주제 (선택). 예: 청소년, 노인, 주거, 일자리, 교육 등") String category
    ) {
        region = normalize(region);
        category = normalize(category);
        log.info("일반 복지 검색 - 지역: {}, 카테고리: {}", region, category);

        // 먼저 넓게 검색
        List<WelfareServiceJpaEntity> results = welfareServiceRepository.searchByRegionAndCategory(
                region, category, null, DEFAULT_LIMIT
        );

        // 결과가 없으면 키워드 검색으로 fallback (지역 조건 유지!)
        if (results.isEmpty() && category != null) {
            log.info("searchByRegionAndCategory 결과 없음 → 지역 조건 유지하며 키워드 검색");
            results = searchByKeywordWithRegionFilter(region, category);
        }

        return formatResults(results, "일반 복지");
    }

    @Tool(description = """
            중앙정부에서 제공하는 복지 서비스를 검색합니다.
            모든 파라미터는 선택 사항입니다. 알고 있는 정보만 입력하세요.
            """)
    public String searchCentralWelfare(
            @ToolParam(description = "생애주기 (선택). 예: 청소년, 청년, 중장년, 노년") String lifeCycle,
            @ToolParam(description = "대상 (선택). 예: 저소득층, 다문화, 장애인, 한부모") String target,
            @ToolParam(description = "주제 (선택). 예: 고용, 주거, 교육, 의료") String theme
    ) {
        lifeCycle = normalize(lifeCycle);
        target = normalize(target);
        theme = normalize(theme);
        log.info("중앙 복지 검색 - 생애주기: {}, 대상: {}, 주제: {}", lifeCycle, target, theme);

        List<WelfareServiceJpaEntity> results = welfareServiceRepository.searchWelfareServices(
                lifeCycle, target, theme, null, null, "CENTRAL", DEFAULT_LIMIT
        );

        // 중앙복지는 지역 제약이 없으므로 일반 키워드 fallback 사용
        results = withKeywordFallbackIfEmpty(results, buildKeyword(lifeCycle, target, theme), null, null);
        return formatResults(results, "중앙복지");
    }

    @Tool(description = """
            지자체에서 제공하는 지역 복지 서비스를 검색합니다.
            모든 파라미터는 선택 사항입니다. 알고 있는 정보만 입력하세요.
            지역 검색이 주 목적이면 searchWelfare를 먼저 사용하세요.
            """)
    public String searchLocalWelfare(
            @ToolParam(description = "생애주기 (선택). 예: 청소년, 청년, 중장년, 노년") String lifeCycle,
            @ToolParam(description = "대상 (선택). 예: 저소득층, 다문화, 장애인, 한부모") String target,
            @ToolParam(description = "주제 (선택). 예: 고용, 주거, 교육, 의료") String theme,
            @ToolParam(description = "시도명 (선택). 예: 대전, 서울, 경기") String sido,
            @ToolParam(description = "시군구명 (선택). 예: 강남구, 수원시") String sigungu
    ) {
        lifeCycle = normalize(lifeCycle);
        target = normalize(target);
        theme = normalize(theme);
        sido = normalize(sido);
        sigungu = normalize(sigungu);
        log.info("지역 복지 검색 - 생애주기: {}, 대상: {}, 주제: {}, 시도: {}, 시군구: {}",
                lifeCycle, target, theme, sido, sigungu);

        List<WelfareServiceJpaEntity> results = welfareServiceRepository.searchWelfareServices(
                lifeCycle, target, theme, sido, sigungu, "LOCAL", DEFAULT_LIMIT
        );

        // 지역 복지는 지역 조건을 유지하며 fallback
        results = withKeywordFallbackIfEmpty(results, buildKeyword(lifeCycle, target, theme), sido, sigungu);
        return formatResults(results, "지역복지");
    }

    @Tool(description = """
            키워드로 복지 서비스를 검색합니다.
            ⚠️ 주의: 사용자가 지역을 언급했다면 searchWelfare를 사용하세요!
            이 도구는 지역 필터링 없이 전국을 검색합니다.
            """)
    public String searchByKeyword(
            @ToolParam(description = "검색 키워드 (예: 일자리, 주거, 교육, 의료)") String keyword
    ) {
        log.info("키워드 복지 검색 (전국) - 키워드 {}", keyword);

        if (keyword == null || keyword.isBlank()) {
            return "검색 키워드를 입력해주세요.";
        }

        List<WelfareServiceJpaEntity> results = welfareServiceRepository.searchByKeyword(keyword, DEFAULT_LIMIT);
        return formatResults(results, "키워드(전국)");
    }

    @Tool(description = """
            특정 복지 서비스명으로 정확하게 검색합니다.
            사용자가 구체적인 서비스명을 언급하거나 특정 서비스의 상세 정보를 요청할 때 사용하세요.
            예: "청년내일채움공제", "국민취업지원제도", "청년도약계좌"

            이 도구는 서비스명에서 정확하게 매칭되는 서비스를 찾아 매우 상세한 정보를 제공합니다.
            """)
    public String searchServiceByName(
            @ToolParam(description = "복지 서비스명 (예: 청년내일채움공제, 국민취업지원제도)") String serviceName
    ) {
        log.info("서비스명 정확 검색 - 서비스명: {}", serviceName);

        if (serviceName == null || serviceName.isBlank()) {
            return "서비스명을 입력해주세요.";
        }

        List<WelfareServiceJpaEntity> results = welfareServiceRepository.searchByKeyword(serviceName, 5);
        return formatDetailedResults(results, "서비스명 검색");
    }

    /**
     * 상세 검색 결과 포맷팅 (서비스명 검색용 - 매우 상세한 정보 제공)
     */
    private String formatDetailedResults(List<WelfareServiceJpaEntity> results, String searchType) {
        if (results.isEmpty()) {
            return String.format("""
                    ========================================
                    ⚠️ 검색 결과: 0건 (결과 없음)
                    ========================================
                    [%s] 해당 이름의 복지 서비스를 찾지 못했습니다.
                    서비스명을 정확하게 입력했는지 확인해주세요.
                    ========================================
                    """, searchType);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append(String.format("✅ 검색 결과: %d건 (결과 있음)\n", results.size()));
        sb.append("========================================\n");
        sb.append(String.format("[%s 검색결과 - 상세정보]\n\n", searchType));

        // 상위 3개만 매우 상세하게 표시
        int displayCount = Math.min(results.size(), 3);

        for (int i = 0; i < displayCount; i++) {
            WelfareServiceJpaEntity w = results.get(i);

            sb.append("========================================\n");
            sb.append(String.format("📋 [%d] %s\n", (i + 1), w.getServiceName()));
            sb.append("========================================\n");

            sb.append("🆔 서비스ID: ").append(nullToDash(w.getServiceId())).append("\n");
            sb.append("📍 지역: ").append(composeRegion(w.getCtpvNm(), w.getSggNm())).append("\n");
            sb.append("🏢 주관기관: ").append(nullToDash(w.getOrganization())).append("\n");
            sb.append("🏛️ 담당부서: ").append(nullToDash(w.getBizChrDeptNm())).append("\n\n");

            sb.append("💡 AI 요약:\n").append(formatParagraph(firstNonNull(w.getAiSummary(), w.getServiceSummary()))).append("\n\n");

            sb.append("👥 지원대상:\n").append(formatParagraph(w.getSupportTargetContent())).append("\n");
            sb.append("→ 생애주기: ").append(nullToDash(w.getLifeCycleArray())).append("\n");
            sb.append("→ 대상구분: ").append(nullToDash(w.getTargetArray())).append("\n");
            sb.append("→ 관심주제: ").append(nullToDash(w.getInterestThemeArray())).append("\n\n");

            sb.append("💰 지원내용:\n").append(formatParagraph(w.getServiceContent())).append("\n");
            sb.append("→ 지원유형: ").append(nullToDash(w.getSupportType())).append("\n");
            sb.append("→ 지원주기: ").append(nullToDash(w.getSupportCycle())).append("\n\n");

            sb.append("✅ 선정기준:\n").append(formatParagraph(w.getSelectionCriteria())).append("\n\n");

            sb.append("📝 신청방법:\n").append(formatParagraph(w.getApplicationMethodContent())).append("\n");
            sb.append("→ 신청방법: ").append(nullToDash(w.getApplicationMethod())).append("\n");
            sb.append("→ 필수서류: ").append(formatParagraph(w.getRequiredDocuments())).append("\n\n");

            sb.append("📞 문의처: ").append(nullToDash(w.getContact())).append("\n");
            sb.append("🔗 상세링크: ").append(nullToDash(w.getDetailLink())).append("\n");

            if (w.getEtc() != null && !w.getEtc().isBlank()) {
                sb.append("📌 기타사항: ").append(formatParagraph(w.getEtc())).append("\n");
            }
            sb.append("\n");
        }

        if (results.size() > displayCount) {
            sb.append(String.format("※ 이 외 %d개의 관련 서비스가 더 있습니다.\n", results.size() - displayCount));
        }

        return sb.toString();
    }

    /**
     * 검색 결과 포맷팅
     */
    private String formatResults(List<WelfareServiceJpaEntity> results, String searchType) {
        if (results.isEmpty()) {
            // 결과 없음을 매우 명확하게 표시
            return String.format("""
                    ========================================
                    ⚠️ 검색 결과: 0건 (결과 없음)
                    ========================================
                    [%s] 검색 결과가 없습니다.
                    해당 조건에 맞는 복지 서비스를 찾지 못했습니다.
                    ========================================
                    """, searchType);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append(String.format("✅ 검색 결과: %d건 (결과 있음)\n", results.size()));
        sb.append("========================================\n");
        sb.append(String.format("[%s 검색결과]\n\n", searchType));

        for (WelfareServiceJpaEntity w : results) {
            sb.append("========================================\n");
            sb.append("서비스ID: ").append(nullToDash(w.getServiceId())).append("\n");
            sb.append("서비스명: ").append(nullToDash(w.getServiceName())).append("\n");

            sb.append("AI요약: ").append(truncate(firstNonNull(w.getAiSummary(), w.getServiceSummary()), 300)).append("\n");
            sb.append("원문요약: ").append(truncate(w.getServiceSummary(), 200)).append("\n");

            sb.append("지역: ").append(composeRegion(w.getCtpvNm(), w.getSggNm())).append("\n");
            sb.append("주관/부서: ").append(nullToDash(w.getOrganization()))
                    .append(" / ").append(nullToDash(w.getDepartment())).append("\n");
            sb.append("담당부서명: ").append(nullToDash(w.getBizChrDeptNm())).append("\n");

            sb.append("지원유형: ").append(nullToDash(w.getSupportType()))
                    .append(" / 주기: ").append(nullToDash(w.getSupportCycle())).append("\n");

            sb.append("신청방법: ").append(nullToDash(w.getApplicationMethod())).append("\n");
            sb.append("신청방법상세: ").append(truncate(w.getApplicationMethodContent(), 200)).append("\n");

            sb.append("지원대상코드(생애주기/대상/주제): ")
                    .append(nullToDash(w.getLifeCycleArray())).append(" / ")
                    .append(nullToDash(w.getTargetArray())).append(" / ")
                    .append(nullToDash(w.getInterestThemeArray())).append("\n");

            sb.append("지원대상상세: ").append(truncate(w.getSupportTargetContent(), 200)).append("\n");
            sb.append("선정기준: ").append(truncate(w.getSelectionCriteria(), 200)).append("\n");
            sb.append("지원내용: ").append(truncate(w.getServiceContent(), 300)).append("\n");
            sb.append("필수서류: ").append(truncate(w.getRequiredDocuments(), 150)).append("\n");
            sb.append("기타: ").append(truncate(w.getEtc(), 150)).append("\n");

            sb.append("문의: ").append(nullToDash(w.getContact())).append("\n");
            sb.append("상세링크: ").append(nullToDash(w.getDetailLink())).append("\n");
            sb.append("\n");
        }

        return sb.toString();
    }

    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    private String formatParagraph(String text) {
        if (text == null || text.isBlank()) {
            return "-";
        }
        // 긴 텍스트를 그대로 반환 (AI가 읽기 좋도록)
        return text.trim();
    }

    private String normalize(String v) {
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    /**
     * 지역 조건을 유지하며 키워드 검색 fallback
     */
    private List<WelfareServiceJpaEntity> withKeywordFallbackIfEmpty(
            List<WelfareServiceJpaEntity> primary,
            String keyword,
            String sido,
            String sigungu
    ) {
        if (primary != null && !primary.isEmpty()) {
            return primary;
        }
        if (keyword == null || keyword.isBlank()) {
            return primary;
        }

        // 지역 조건이 있으면 지역 조건을 유지하며 검색
        if (sido != null || sigungu != null) {
            log.info("지역 조건 유지 키워드 fallback - 시도: {}, 시군구: {}, 키워드: {}", sido, sigungu, keyword);
            return welfareServiceRepository.searchByKeywordWithRegion(sido, sigungu, keyword, DEFAULT_LIMIT);
        }

        // 지역 조건이 없으면 전국 검색
        return welfareServiceRepository.searchByKeyword(keyword, DEFAULT_LIMIT);
    }

    /**
     * 지역 필터를 유지하며 키워드 검색 (일반 복지용)
     */
    private List<WelfareServiceJpaEntity> searchByKeywordWithRegionFilter(String region, String keyword) {
        if (region == null || region.isBlank()) {
            // 지역이 없으면 전국 검색
            return welfareServiceRepository.searchByKeyword(keyword, DEFAULT_LIMIT);
        }

        // 지역이 있으면 시도명으로 간주하고 검색
        log.info("지역 조건 유지 키워드 검색 - 지역: {}, 키워드: {}", region, keyword);
        return welfareServiceRepository.searchByKeywordWithRegion(region, null, keyword, DEFAULT_LIMIT);
    }

    private String buildKeyword(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p != null && !p.isBlank()) {
                if (!sb.isEmpty()) sb.append(' ');
                sb.append(p.trim());
            }
        }
        String result = sb.toString();
        return result.isBlank() ? null : result;
    }

    private String nullToDash(Object obj) {
        return obj == null ? "-" : obj.toString();
    }

    private String composeRegion(String ctpv, String sgg) {
        if (ctpv == null && sgg == null) return "-";
        if (sgg == null) return ctpv;
        return ctpv + " " + sgg;
    }

    private String firstNonNull(String a, String b) {
        return a != null ? a : b != null ? b : "-";
    }
}
