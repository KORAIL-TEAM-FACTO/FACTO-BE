package team.java.facto_be.domain.bookmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.java.facto_be.domain.bookmark.dto.response.BookmarkResponse;
import team.java.facto_be.domain.bookmark.service.BookmarkService;

import java.util.List;

/**
 * 즐겨찾기 REST API 컨트롤러.
 *
 * <p>복지 서비스 즐겨찾기 추가, 삭제, 조회 API를 제공합니다.
 * 모든 엔드포인트는 JWT 인증이 필요합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * 복지 서비스를 즐겨찾기에 추가합니다.
     *
     * <p>프론트엔드 호출 시점: 사용자가 복지 서비스 상세 페이지에서 "즐겨찾기 추가" 버튼 클릭
     *
     * @param welfareServiceId 복지 서비스 ID
     * @return 201 Created (바디 없음)
     * @throws IllegalArgumentException 이미 즐겨찾기에 추가된 경우
     */
    @PostMapping("/{welfareServiceId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBookmark(@PathVariable String welfareServiceId) {
        bookmarkService.addBookmark(welfareServiceId);
    }

    /**
     * 복지 서비스를 즐겨찾기에서 제거합니다.
     *
     * <p>프론트엔드 호출 시점: 사용자가 "즐겨찾기 삭제" 버튼 클릭
     *
     * @param welfareServiceId 복지 서비스 ID
     * @return 204 No Content (바디 없음)
     */
    @DeleteMapping("/{welfareServiceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookmark(@PathVariable String welfareServiceId) {
        bookmarkService.removeBookmark(welfareServiceId);
    }

    /**
     * 현재 사용자의 즐겨찾기 목록을 조회합니다.
     *
     * <p>프론트엔드 호출 시점: 사용자가 "내 즐겨찾기" 페이지 진입
     *
     * @return 즐겨찾기 목록 (최신순)
     */
    @GetMapping
    public List<BookmarkResponse> getMyBookmarks() {
        return bookmarkService.getMyBookmarks();
    }

    /**
     * 특정 복지 서비스가 즐겨찾기에 추가되어 있는지 확인합니다.
     *
     * <p>프론트엔드 호출 시점: 복지 서비스 상세 페이지에서 즐겨찾기 버튼 상태 표시
     * (하트 아이콘 채우기/비우기 등)
     *
     * @param welfareServiceId 복지 서비스 ID
     * @return true: 즐겨찾기 추가됨, false: 추가 안됨
     */
    @GetMapping("/{welfareServiceId}/check")
    public boolean checkBookmark(@PathVariable String welfareServiceId) {
        return bookmarkService.isBookmarked(welfareServiceId);
    }
}
