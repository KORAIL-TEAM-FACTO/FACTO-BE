package team.java.facto_be.domain.bookmark.repository.custom;

import team.java.facto_be.domain.bookmark.entity.BookmarkJpaEntity;

import java.util.List;
import java.util.Optional;

/**
 * 즐겨찾기 커스텀 Repository 인터페이스.
 *
 * <p>QueryDSL을 사용한 복잡한 쿼리를 정의합니다.
 */
public interface BookmarkRepositoryCustom {

    /**
     * 사용자의 즐겨찾기 목록을 최신순으로 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 즐겨찾기 목록 (최신순)
     */
    List<BookmarkJpaEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 사용자와 복지 서비스 ID로 즐겨찾기를 조회합니다.
     *
     * @param userId 사용자 ID
     * @param welfareServiceId 복지 서비스 ID
     * @return 즐겨찾기 (Optional)
     */
    Optional<BookmarkJpaEntity> findByUserIdAndWelfareServiceId(Long userId, String welfareServiceId);

    /**
     * 즐겨찾기 존재 여부를 확인합니다.
     *
     * @param userId 사용자 ID
     * @param welfareServiceId 복지 서비스 ID
     * @return 존재 여부
     */
    boolean existsByUserIdAndWelfareServiceId(Long userId, String welfareServiceId);

    /**
     * 사용자와 복지 서비스 ID로 즐겨찾기를 삭제합니다.
     *
     * @param userId 사용자 ID
     * @param welfareServiceId 복지 서비스 ID
     */
    void deleteByUserIdAndWelfareServiceId(Long userId, String welfareServiceId);
}
