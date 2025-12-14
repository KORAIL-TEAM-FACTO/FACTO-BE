package team.java.facto_be.domain.welfare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.java.facto_be.domain.welfare.entity.WelfareServiceJpaEntity;
import team.java.facto_be.domain.welfare.repository.custom.WelfareServiceRepositoryCustom;

/**
 * 복지 서비스 Repository.
 *
 * <p>JpaRepository와 QueryDSL 기반 커스텀 메서드를 모두 제공합니다.
 */
public interface WelfareServiceRepository extends JpaRepository<WelfareServiceJpaEntity, String>, WelfareServiceRepositoryCustom {
}
