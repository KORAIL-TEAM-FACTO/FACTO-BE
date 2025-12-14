package team.java.facto_be.domain.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.java.facto_be.domain.bookmark.dto.response.BookmarkResponse;
import team.java.facto_be.domain.bookmark.entity.BookmarkJpaEntity;
import team.java.facto_be.domain.bookmark.repository.BookmarkRepository;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.domain.user.facade.UserFacade;

import java.util.List;

/**
 * 즐겨찾기 비즈니스 로직 서비스.
 *
 * <p>복지 서비스 즐겨찾기 추가, 삭제, 조회 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserFacade userFacade;

    /**
     * 복지 서비스를 즐겨찾기에 추가합니다.
     *
     * <p>동작 흐름:
     * 1. JWT 토큰에서 현재 로그인한 사용자 정보 추출
     * 2. 이미 즐겨찾기에 추가되어 있는지 확인
     * 3. 중복이면 예외 발생, 아니면 DB에 저장
     *
     * @param welfareServiceId 복지 서비스 ID
     * @throws IllegalArgumentException 이미 즐겨찾기에 추가된 경우
     */
    @Transactional
    public void addBookmark(String welfareServiceId) {
        // 1. 현재 로그인한 사용자 가져오기
        UserJpaEntity user = userFacade.currentUser();

        // 2. 중복 체크
        if (bookmarkRepository.existsByUserIdAndWelfareServiceId(user.getId(), welfareServiceId)) {
            throw new IllegalArgumentException("이미 즐겨찾기에 추가된 복지 서비스입니다.");
        }

        // 3. 즐겨찾기 엔티티 생성 및 저장
        BookmarkJpaEntity bookmark = BookmarkJpaEntity.builder()
                .userId(user.getId())
                .welfareServiceId(welfareServiceId)
                .build();

        bookmarkRepository.save(bookmark);
    }

    /**
     * 복지 서비스를 즐겨찾기에서 제거합니다.
     *
     * <p>동작 흐름:
     * 1. JWT 토큰에서 현재 로그인한 사용자 정보 추출
     * 2. DB에서 해당 사용자의 즐겨찾기 삭제
     * 3. 존재하지 않아도 예외 발생하지 않음 (멱등성)
     *
     * @param welfareServiceId 복지 서비스 ID
     */
    @Transactional
    public void removeBookmark(String welfareServiceId) {
        // 1. 현재 로그인한 사용자 가져오기
        UserJpaEntity user = userFacade.currentUser();

        // 2. 즐겨찾기 삭제
        bookmarkRepository.deleteByUserIdAndWelfareServiceId(user.getId(), welfareServiceId);
    }

    /**
     * 현재 사용자의 즐겨찾기 목록을 조회합니다.
     *
     * <p>동작 흐름:
     * 1. JWT 토큰에서 현재 로그인한 사용자 정보 추출
     * 2. DB에서 해당 사용자의 즐겨찾기 목록 조회 (최신순)
     * 3. BookmarkResponse DTO로 변환하여 반환
     *
     * @return 즐겨찾기 목록 (최신순)
     */
    @Transactional(readOnly = true)
    public List<BookmarkResponse> getMyBookmarks() {
        // 1. 현재 로그인한 사용자 가져오기
        UserJpaEntity user = userFacade.currentUser();

        // 2. 즐겨찾기 목록 조회 및 DTO 변환
        return bookmarkRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(BookmarkResponse::from)
                .toList();
    }

    /**
     * 특정 복지 서비스가 즐겨찾기에 추가되어 있는지 확인합니다.
     *
     * <p>동작 흐름:
     * 1. JWT 토큰에서 현재 로그인한 사용자 정보 추출
     * 2. DB에서 즐겨찾기 존재 여부 확인
     *
     * @param welfareServiceId 복지 서비스 ID
     * @return 즐겨찾기 여부 (true: 추가됨, false: 추가 안됨)
     */
    @Transactional(readOnly = true)
    public boolean isBookmarked(String welfareServiceId) {
        // 1. 현재 로그인한 사용자 가져오기
        UserJpaEntity user = userFacade.currentUser();

        // 2. 즐겨찾기 존재 여부 확인
        return bookmarkRepository.existsByUserIdAndWelfareServiceId(user.getId(), welfareServiceId);
    }
}
