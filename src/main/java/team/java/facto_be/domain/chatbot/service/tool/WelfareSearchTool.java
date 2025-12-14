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

        // 결과가 없으면 키워드 검색으로 fallback
        if (results.isEmpty() && category != null) {
            results = welfareServiceRepository.searchByKeyword(category, DEFAULT_LIMIT);
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

        results = withKeywordFallbackIfEmpty(results, buildKeyword(lifeCycle, target, theme));
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

        results = withKeywordFallbackIfEmpty(results, buildKeyword(lifeCycle, target, theme, sido, sigungu));
        return formatResults(results, "지역복지");
    }

    @Tool(description = "키워드로 모든 복지 서비스를 검색합니다.")
    public String searchByKeyword(
            @ToolParam(description = "검색 키워드 (예: 일자리, 주거, 교육, 의료)") String keyword
    ) {
        log.info("키워드 복지 검색 - 키워드 {}", keyword);

        if (keyword == null || keyword.isBlank()) {
            return "검색 키워드를 입력해주세요.";
        }

        List<WelfareServiceJpaEntity> results = welfareServiceRepository.searchByKeyword(keyword, DEFAULT_LIMIT);
        return formatResults(results, "키워드");
    }

    /**
     * 검색 결과 포맷팅
     */
    private String formatResults(List<WelfareServiceJpaEntity> results, String searchType) {
        if (results.isEmpty()) {
            return String.format("[%s 검색 결과가 없습니다.", searchType);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s 검색결과 - %d건\n\n", searchType, results.size()));

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

    private String normalize(String v) {
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    private List<WelfareServiceJpaEntity> withKeywordFallbackIfEmpty(List<WelfareServiceJpaEntity> primary, String keyword) {
        if (primary != null && !primary.isEmpty()) {
            return primary;
        }
        if (keyword == null || keyword.isBlank()) {
            return primary;
        }
        List<WelfareServiceJpaEntity> fallback = welfareServiceRepository.searchByKeyword(keyword, DEFAULT_LIMIT);
        return fallback;
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
