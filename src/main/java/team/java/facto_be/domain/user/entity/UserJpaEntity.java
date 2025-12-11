package team.java.facto_be.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import team.java.facto_be.global.security.jwt.domain.entity.types.Role;

/**
 * 사용자 JPA 엔티티.
 * 데이터베이스 tbl_user 테이블과 매핑되는 영속성 엔티티입니다.
 * 도메인 모델(User)과 분리되어 인프라 계층의 관심사를 처리합니다.
 */
@Entity(name = "UserJpaEntity")
@Table(name = "tbl_user")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "life_cycle_code", nullable = false, length = 3)
    private String lifeCycleCode;

    @Column(name = "household_status_code", nullable = false, length = 3)
    private String householdStatusCode;

    @Column(name = "interest_theme_code", nullable = false, length = 3)
    private String interestThemeCode;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "sido_name", nullable = false, length = 50)
    private String sidoName;

    @Column(name = "sigungu_name", nullable = false, length = 50)
    private String sigunguName;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public void updateProfile(String name,
                              String lifeCycleCode,
                              String householdStatusCode,
                              String interestThemeCode,
                              Integer age,
                              String sidoName,
                              String sigunguName) {
        this.name = name;
        this.lifeCycleCode = lifeCycleCode;
        this.householdStatusCode = householdStatusCode;
        this.interestThemeCode = interestThemeCode;
        this.age = age;
        this.sidoName = sidoName;
        this.sigunguName = sigunguName;
    }
}
