package team.java.facto_be.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import team.java.facto_be.global.entity.BaseTimeEntity;

/**
 * 사용자 프로필 변경 이력 JPA 엔티티.
 *
 * <p>프로필 수정 시 이전 값을 저장하여 변경 이력을 추적합니다.
 * 특히 지역 변경에 따른 복지 서비스 개수 차이를 조회하는데 사용됩니다.
 */
@Entity(name = "UserProfileHistoryJpaEntity")
@Table(name = "tbl_user_profile_history")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileHistoryJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "old_name", length = 100)
    private String oldName;

    @Column(name = "new_name", length = 100)
    private String newName;

    @Column(name = "old_life_cycle", length = 20)
    private String oldLifeCycle;

    @Column(name = "new_life_cycle", length = 20)
    private String newLifeCycle;

    @Column(name = "old_household_status", columnDefinition = "TEXT")
    private String oldHouseholdStatus;

    @Column(name = "new_household_status", columnDefinition = "TEXT")
    private String newHouseholdStatus;

    @Column(name = "old_interest_theme", columnDefinition = "TEXT")
    private String oldInterestTheme;

    @Column(name = "new_interest_theme", columnDefinition = "TEXT")
    private String newInterestTheme;

    @Column(name = "old_age")
    private Integer oldAge;

    @Column(name = "new_age")
    private Integer newAge;

    @Column(name = "old_sido_name", length = 50)
    private String oldSidoName;

    @Column(name = "new_sido_name", length = 50)
    private String newSidoName;

    @Column(name = "old_sigungu_name", length = 50)
    private String oldSigunguName;

    @Column(name = "new_sigungu_name", length = 50)
    private String newSigunguName;
}
