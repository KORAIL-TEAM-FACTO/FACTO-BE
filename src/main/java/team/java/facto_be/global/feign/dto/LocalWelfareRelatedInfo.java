package team.java.facto_be.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocalWelfareRelatedInfo(
        @JsonProperty("wlfareInfoDtlCd") String detailCode,
        @JsonProperty("wlfareInfoReldNm") String name,
        @JsonProperty("wlfareInfoReldCn") String content
) { }
