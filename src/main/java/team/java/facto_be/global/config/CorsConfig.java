package team.java.facto_be.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * CORS(Cross-Origin Resource Sharing) 설정.
 *
 * <p>프론트엔드에서 백엔드 API 호출 시 CORS 에러를 방지합니다.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 인증 정보 허용 (쿠키, Authorization 헤더 등)
        config.setAllowCredentials(true);

        // 허용할 Origin 설정
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://facto-fe.pages.dev"
        ));


        // 모든 헤더 허용
        config.addAllowedHeader("*");

        // 모든 HTTP 메서드 허용
        config.addAllowedMethod("*");

        // 노출할 헤더 설정 (프론트엔드에서 접근 가능)
        config.setExposedHeaders(List.of("Authorization"));

        // 모든 경로에 대해 CORS 설정 적용
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
