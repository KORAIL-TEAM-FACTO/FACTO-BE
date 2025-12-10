package team.java.facto_be.global.feign.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.java.facto_be.global.feign.error.Custom5xxRetryer;
import team.java.facto_be.global.feign.error.CustomErrorDecoder;


/**
 * 전역 Feign 설정: 커스텀 에러 디코더와 재시도 정책을 등록한다.
 */
@EnableFeignClients(basePackages = {"team.java.facto_be"})
@Configuration
public class FeignConfig {

    /**
     * Feign 호출 실패 시 사용할 ErrorDecoder 빈.
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    /**
     * 5xx 오류에 대한 재시도 로직을 정의한 Retryer 빈.
     */
    @Bean
    public Retryer retryer() {
        return new Custom5xxRetryer();
    }
}
