package team.java.facto_be.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import team.java.facto_be.global.security.jwt.JwtTokenFilter;
import team.java.facto_be.global.security.jwt.JwtTokenProvider;

/**
 * 커스텀 보안 필터(JWT, 예외 처리)를 SecurityFilterChain에 등록하는 설정.
 */
@RequiredArgsConstructor
@Configuration
public class FilterConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    /**
     * JWT 필터와 전역 예외 필터를 체인에 삽입한다.
     */
    @Override
    public void configure(HttpSecurity http){
        JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider);

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
