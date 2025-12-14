package team.java.facto_be.domain.recentview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import team.java.facto_be.global.entity.BaseTimeEntity;

import java.time.LocalDateTime;

/**
 * 최근 본 복지 서비스 JPA 엔티티.
 *
 * <p>사용자별 최근 본 복지 서비스 이력을 저장합니다. (최대 100개)
 * 같은 복지 서비스를 다시 보면 새로 추가하지 않고 조회 시간만 업데이트합니다.
 *
 * <p>작동 방식:
 * 1. 사용자가 복지 서비스 상세 페이지 진입
 * 2. 프론트엔드에서 POST /recent-views/{welfareServiceId} 호출
 * 3. 이미 본 적 있으면 viewedAt만 업데이트, 없으면 새로 저장
 * 4. 사용자의 최근 본 기록이 100개 초과하면 가장 오래된 것 자동 삭제
 * 5. 이 데이터를 집계하여 인기 복지 서비스 TOP 10 제공
 *
 * <p>필드 설명:
 * - createdAt (BaseTimeEntity): 레코드가 처음 생성된 시간
 * - updatedAt (BaseTimeEntity): 레코드가 마지막으로 수정된 시간
 * - viewedAt: 사용자가 실제로 복지 서비스를 본 시간 (비즈니스 로직용)
 *
 * @see team.java.facto_be.domain.recentview.service.RecentViewService
 */
@Entity(name = "RecentViewJpaEntity")
@Table(
    name = "tbl_recent_view",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "welfare_service_id"})
)
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentViewJpaEntity extends BaseTimeEntity {

    /**
     * 최근 본 기록 고유 ID (자동 증가)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 ID
     * UserJpaEntity의 id를 참조하지만 외래키 제약 없이 저장
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 복지 서비스 ID
     * 다른 팀원이 개발하는 복지 서비스의 고유 ID
     * 이 ID로 인기 복지 서비스 통계를 집계
     */
    @Column(name = "welfare_service_id", nullable = false)
    private String welfareServiceId;

    /**
     * 복지 서비스를 본 시간
     * 중복 조회 시 이 값만 업데이트됨
     * 인기 복지 서비스 집계 시 이 시간 기준으로 필터링 (최근 7일 등)
     */
    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    /**
     * 조회 시간을 업데이트합니다.
     * 같은 복지 서비스를 다시 볼 때 호출됩니다.
     *
     * @param viewedAt 새로운 조회 시간
     */
    public void updateViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }
}
