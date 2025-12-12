package team.java.facto_be.domain.welfare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.domain.user.facade.UserFacade;
import team.java.facto_be.domain.welfare.dto.MostViewedWelfareResponse;
import team.java.facto_be.domain.welfare.dto.RecentWelfareViewResponse;
import team.java.facto_be.domain.welfare.entity.RecentWelfareView;
import team.java.facto_be.domain.welfare.repository.RecentWelfareViewRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentWelfareViewService {

    private final RecentWelfareViewRepository recentWelfareViewRepository;
    private final UserFacade userFacade;

    @Transactional
    public void recordView(String servId, String servNm) {
        UserJpaEntity user = userFacade.currentUser();
        recentWelfareViewRepository.findByUserAndServId(user, servId)
                .ifPresentOrElse(existing -> existing.refreshView(servNm),
                        () -> recentWelfareViewRepository.save(
                                RecentWelfareView.builder()
                                        .user(user)
                                        .servId(servId)
                                        .servNm(servNm)
                                        .lastViewedAt(LocalDateTime.now())
                                        .viewCount(1)
                                        .build()
                        ));
    }

    @Transactional(readOnly = true)
    public List<RecentWelfareViewResponse> getRecentViews() {
        UserJpaEntity user = userFacade.currentUser();
        return recentWelfareViewRepository.findTop100ByUserOrderByLastViewedAtDesc(user)
                .stream()
                .map(RecentWelfareViewResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MostViewedWelfareResponse> getMostViewed() {
        UserJpaEntity user = userFacade.currentUser();
        return recentWelfareViewRepository.findTop10ByUserOrderByViewCountDescLastViewedAtDesc(user)
                .stream()
                .map(entity -> new MostViewedWelfareResponse(
                        entity.getServId(),
                        entity.getServNm(),
                        entity.getViewCount(),
                        entity.getLastViewedAt()
                ))
                .toList();
    }
}
