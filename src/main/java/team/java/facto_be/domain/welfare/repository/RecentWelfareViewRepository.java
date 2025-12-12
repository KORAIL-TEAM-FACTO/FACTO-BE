package team.java.facto_be.domain.welfare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.domain.welfare.entity.RecentWelfareView;

import java.util.List;
import java.util.Optional;

public interface RecentWelfareViewRepository extends JpaRepository<RecentWelfareView, Long> {

    Optional<RecentWelfareView> findByUserAndServId(UserJpaEntity user, String servId);

    List<RecentWelfareView> findTop100ByUserOrderByLastViewedAtDesc(UserJpaEntity user);

    List<RecentWelfareView> findTop10ByUserOrderByViewCountDescLastViewedAtDesc(UserJpaEntity user);
}
