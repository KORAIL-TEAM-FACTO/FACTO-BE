package team.java.facto_be.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocalWelfareRelatedInfo(
        @JacksonXmlProperty(localName = "wlfareInfoDtlCd") String detailCode,
        @JacksonXmlProperty(localName = "wlfareInfoReldNm") String name,
        @JacksonXmlProperty(localName = "wlfareInfoReldCn") String content
) { }
