package team.java.facto_be.domain.welfare.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import team.java.facto_be.global.entity.BaseTimeEntity;

/**
 * 복지 서비스 조회 이력 JPA 엔티티.
 *
 * <p>사용자가 복지 서비스를 조회한 이력을 기록합니다.
 * 실시간 조회수 집계 및 통계 분석에 사용됩니다.
 */
@Entity(name = "WelfareViewHistoryJpaEntity")
@Table(name = "tbl_welfare_view_history", indexes = {
        @Index(name = "idx_service_id", columnList = "service_id"),
        @Index(name = "idx_user_service", columnList = "user_id, service_id, viewed_at")
})
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WelfareViewHistoryJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 복지 서비스 ID
     */
    @Column(name = "service_id", nullable = false, length = 50)
    private String serviceId;

    /**
     * 조회한 사용자 ID (비로그인 시 null)
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 조회 시간 (created_at과 동일하지만 인덱스 성능을 위해 별도 컬럼)
     */
    @Column(name = "viewed_at", nullable = false)
    private java.time.LocalDateTime viewedAt;

    /**
     * IP 주소 (선택적, 향후 확장용)
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
}
