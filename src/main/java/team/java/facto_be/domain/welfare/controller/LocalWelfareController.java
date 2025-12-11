package team.java.facto_be.domain.welfare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.java.facto_be.domain.welfare.service.LocalWelfareService;
import team.java.facto_be.global.feign.dto.LocalWelfareResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/welfare")
public class LocalWelfareController {

    private final LocalWelfareService localWelfareService;

    @GetMapping("/local/by-user-region")
    public LocalWelfareResponse getByUserRegion() {
        return localWelfareService.fetchByUserRegion();
    }
}
