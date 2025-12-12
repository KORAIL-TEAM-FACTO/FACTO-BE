package team.java.facto_be.domain.welfare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.domain.user.facade.UserFacade;
import team.java.facto_be.global.feign.client.LocalWelfareClient;
import team.java.facto_be.global.feign.dto.LocalWelfareDetailResponse;
import team.java.facto_be.global.feign.dto.LocalWelfareResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocalWelfareService {

    private final LocalWelfareClient localWelfareClient;
    private final UserFacade userFacade;

    @Value("${local-welfare.service-key}")
    private String serviceKey;

    public LocalWelfareResponse fetchByUserRegion() {
        UserJpaEntity user = userFacade.currentUser();

        return localWelfareClient.getWelfareList(
                serviceKey,
                null,
                null,
                null,
                null,
                null,
                null,
                user.getSidoName(),
                user.getSigunguName(),
                null,
                null,
                null
        );
    }

    @Transactional
    public LocalWelfareDetailResponse fetchDetail(String servId) {
        if (servId == null || servId.isBlank()) {
            throw new IllegalArgumentException("서비스 ID는 필수입니다.");
        }
        userFacade.currentUser(); // ensure authenticated
        return localWelfareClient.getWelfareDetail(serviceKey, servId);
    }
}


