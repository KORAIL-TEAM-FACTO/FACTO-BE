package team.java.facto_be.global.feign.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
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

    /**
     * XML 응답을 처리하기 위한 Feign Decoder 빈.
     */
    @Bean
    public Decoder feignDecoder() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2XmlHttpMessageConverter xmlConverter = new MappingJackson2XmlHttpMessageConverter(xmlMapper);

        ObjectFactory<HttpMessageConverters> messageConverters = () ->
            new HttpMessageConverters(xmlConverter);

        return new SpringDecoder(messageConverters);
    }
}
