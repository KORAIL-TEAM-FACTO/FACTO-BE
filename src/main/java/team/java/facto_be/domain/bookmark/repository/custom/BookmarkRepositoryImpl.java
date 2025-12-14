package team.java.facto_be.domain.bookmark.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import team.java.facto_be.domain.bookmark.entity.BookmarkJpaEntity;

import java.util.List;
import java.util.Optional;

import static team.java.facto_be.domain.bookmark.entity.QBookmarkJpaEntity.bookmarkJpaEntity;

/**
 * 즐겨찾기 커스텀 Repository 구현체.
 *
 * <p>QueryDSL을 사용하여 타입 안전한 쿼리를 작성합니다.
 */
@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BookmarkJpaEntity> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return queryFactory
                .selectFrom(bookmarkJpaEntity)
                .where(bookmarkJpaEntity.userId.eq(userId))
                .orderBy(bookmarkJpaEntity.createdAt.desc())
                .fetch();
    }

    @Override
    public Optional<BookmarkJpaEntity> findByUserIdAndWelfareServiceId(Long userId, String welfareServiceId) {
        BookmarkJpaEntity result = queryFactory
                .selectFrom(bookmarkJpaEntity)
                .where(
                        bookmarkJpaEntity.userId.eq(userId),
                        bookmarkJpaEntity.welfareServiceId.eq(welfareServiceId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsByUserIdAndWelfareServiceId(Long userId, String welfareServiceId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(bookmarkJpaEntity)
                .where(
                        bookmarkJpaEntity.userId.eq(userId),
                        bookmarkJpaEntity.welfareServiceId.eq(welfareServiceId)
                )
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public void deleteByUserIdAndWelfareServiceId(Long userId, String welfareServiceId) {
        queryFactory
                .delete(bookmarkJpaEntity)
                .where(
                        bookmarkJpaEntity.userId.eq(userId),
                        bookmarkJpaEntity.welfareServiceId.eq(welfareServiceId)
                )
                .execute();
    }
}
