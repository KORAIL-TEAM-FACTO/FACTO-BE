package team.java.facto_be.domain.welfare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.java.facto_be.domain.welfare.dto.RecentWelfareViewResponse;
import team.java.facto_be.domain.welfare.dto.MostViewedWelfareResponse;
import team.java.facto_be.domain.welfare.dto.WelfareBookmarkRequest;
import team.java.facto_be.domain.welfare.dto.WelfareBookmarkResponse;
import team.java.facto_be.domain.welfare.service.LocalWelfareService;
import team.java.facto_be.domain.welfare.service.RecentWelfareViewService;
import team.java.facto_be.domain.welfare.service.WelfareBookmarkService;
import team.java.facto_be.global.feign.dto.LocalWelfareDetailResponse;
import team.java.facto_be.global.feign.dto.LocalWelfareResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/welfare")
public class LocalWelfareController {

    private final LocalWelfareService localWelfareService;
    private final RecentWelfareViewService recentWelfareViewService;
    private final WelfareBookmarkService welfareBookmarkService;

    @GetMapping("/local/by-user-region")
    public LocalWelfareResponse getByUserRegion() {
        return localWelfareService.fetchByUserRegion();
    }

    @GetMapping("/local/detail")
    public LocalWelfareDetailResponse getDetail(@RequestParam("servId") String servId) {
        LocalWelfareDetailResponse detail = localWelfareService.fetchDetail(servId);
        recentWelfareViewService.recordView(servId, detail.servNm());
        return detail;
    }

    @GetMapping("/local/recent")
    public java.util.List<RecentWelfareViewResponse> getRecentViews() {
        return recentWelfareViewService.getRecentViews();
    }

    @GetMapping("/local/most-viewed")
    public java.util.List<MostViewedWelfareResponse> getMostViewed() {
        return recentWelfareViewService.getMostViewed();
    }

    @PostMapping("/local/bookmarks")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBookmark(@RequestBody @jakarta.validation.Valid WelfareBookmarkRequest request) {
        welfareBookmarkService.addBookmark(request);
    }

    @DeleteMapping("/local/bookmarks")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookmark(@RequestParam("servId") String servId) {
        welfareBookmarkService.removeBookmark(servId);
    }

    @GetMapping("/local/bookmarks")
    public java.util.List<WelfareBookmarkResponse> getBookmarks() {
        return welfareBookmarkService.getBookmarks();
    }
}
