package team.java.facto_be.domain.recentview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.java.facto_be.domain.recentview.entity.RecentViewJpaEntity;
import team.java.facto_be.domain.recentview.repository.custom.RecentViewRepositoryCustom;

/**
 * 최근 본 복지 서비스 Repository.
 *
 * <p>JpaRepository와 QueryDSL 기반 커스텀 메서드를 모두 제공합니다.
 */
public interface RecentViewRepository extends JpaRepository<RecentViewJpaEntity, Long>, RecentViewRepositoryCustom {
}
