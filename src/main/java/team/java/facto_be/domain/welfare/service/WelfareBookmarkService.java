package team.java.facto_be.domain.welfare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.java.facto_be.domain.user.facade.UserFacade;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.domain.welfare.dto.WelfareBookmarkRequest;
import team.java.facto_be.domain.welfare.dto.WelfareBookmarkResponse;
import team.java.facto_be.domain.welfare.entity.WelfareBookmark;
import team.java.facto_be.domain.welfare.repository.WelfareBookmarkRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WelfareBookmarkService {

    private final WelfareBookmarkRepository welfareBookmarkRepository;
    private final UserFacade userFacade;

    @Transactional
    public void addBookmark(WelfareBookmarkRequest request) {
        UserJpaEntity user = userFacade.currentUser();
        welfareBookmarkRepository.findByUserAndServId(user, request.servId())
                .ifPresentOrElse(existing -> existing.updateServNm(request.servNm()),
                        () -> welfareBookmarkRepository.save(
                                WelfareBookmark.builder()
                                        .user(user)
                                        .servId(request.servId())
                                        .servNm(request.servNm())
                                        .build()
                        ));
    }

    @Transactional
    public void removeBookmark(String servId) {
        UserJpaEntity user = userFacade.currentUser();
        welfareBookmarkRepository.findByUserAndServId(user, servId)
                .ifPresent(welfareBookmarkRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<WelfareBookmarkResponse> getBookmarks() {
        UserJpaEntity user = userFacade.currentUser();
        return welfareBookmarkRepository.findTop100ByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(bookmark -> new WelfareBookmarkResponse(
                        bookmark.getServId(),
                        bookmark.getServNm(),
                        bookmark.getCreatedAt()
                ))
                .toList();
    }
}
