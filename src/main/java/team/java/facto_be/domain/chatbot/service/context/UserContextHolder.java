package team.java.facto_be.domain.chatbot.service.context;

/**
 * ThreadLocal을 사용하여 현재 요청의 사용자 ID를 저장하고 조회하는 유틸리티 클래스.
 *
 * <p>Spring AI의 Tool 호출 시 SecurityContext가 전파되지 않는 문제를 해결하기 위해
 * ChatService 레벨에서 userId를 ThreadLocal에 저장하고,
 * Tool(PersonalizedWelfareRecommendationTool)에서 조회할 수 있도록 합니다.
 *
 * <p>사용 패턴:
 * <pre>
 * try {
 *     UserContextHolder.setUserId(userId);
 *     // Tool 호출이 포함된 작업 수행
 * } finally {
 *     UserContextHolder.clear();  // 반드시 정리
 * }
 * </pre>
 *
 * <p>주의: 반드시 finally 블록에서 clear()를 호출하여 메모리 누수를 방지해야 합니다.
 */
public class UserContextHolder {

    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    /**
     * 현재 스레드에 사용자 ID를 설정합니다.
     *
     * @param userId 사용자 ID (null 가능)
     */
    public static void setUserId(Long userId) {
        if (userId != null) {
            userIdHolder.set(userId);
        } else {
            userIdHolder.remove();
        }
    }

    /**
     * 현재 스레드의 사용자 ID를 조회합니다.
     *
     * @return 사용자 ID (설정되지 않았으면 null)
     */
    public static Long getUserId() {
        return userIdHolder.get();
    }

    /**
     * 현재 스레드의 사용자 ID를 제거합니다.
     *
     * <p>메모리 누수 방지를 위해 반드시 작업 완료 후 호출해야 합니다.
     */
    public static void clear() {
        userIdHolder.remove();
    }
}
