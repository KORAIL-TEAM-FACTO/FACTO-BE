package team.java.facto_be.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.java.facto_be.domain.bookmark.entity.BookmarkJpaEntity;
import team.java.facto_be.domain.bookmark.repository.custom.BookmarkRepositoryCustom;

/**
 * 즐겨찾기 Repository.
 *
 * <p>JpaRepository와 QueryDSL 기반 커스텀 메서드를 모두 제공합니다.
 */
public interface BookmarkRepository extends JpaRepository<BookmarkJpaEntity, Long>, BookmarkRepositoryCustom {
}
