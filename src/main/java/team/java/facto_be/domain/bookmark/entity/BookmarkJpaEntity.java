package team.java.facto_be.domain.bookmark.entity;

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

/**
 * 즐겨찾기 JPA 엔티티.
 *
 * <p>사용자별 복지 서비스 즐겨찾기 정보를 저장합니다.
 * 각 사용자는 동일한 복지 서비스를 중복으로 즐겨찾기할 수 없습니다.
 *
 * <p>작동 방식:
 * 1. 사용자가 복지 서비스 상세 페이지에서 "즐겨찾기 추가" 클릭
 * 2. POST /bookmarks/{welfareServiceId} 호출
 * 3. 현재 로그인한 사용자 ID + 복지 서비스 ID 조합으로 DB에 저장
 * 4. BaseTimeEntity의 createdAt, updatedAt 자동 생성
 *
 * @see team.java.facto_be.domain.bookmark.service.BookmarkService
 */
@Entity(name = "BookmarkJpaEntity")
@Table(
    name = "tbl_bookmark",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "welfare_service_id"})
)
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkJpaEntity extends BaseTimeEntity {

    /**
     * 즐겨찾기 고유 ID (자동 증가)
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
     * 실제 복지 서비스 정보는 이 ID로 별도 조회
     */
    @Column(name = "welfare_service_id", nullable = false)
    private String welfareServiceId;
}
