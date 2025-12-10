package team.java.facto_be.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocalWelfareItem(
        @JsonProperty("servDgst") String summary,
        @JsonProperty("servDtlLink") String detailLink,
        @JsonProperty("lifeNmArray") String lifeCycle,
        @JsonProperty("intrsThemaNmArray") String interestTheme,
        @JsonProperty("sprtCycNm") String supportCycle,
        @JsonProperty("srvPvsnNm") String provider,
        @JsonProperty("aplyMtdNm") String applyMethod,
        @JsonProperty("inqNum") String inquiryCount,
        @JsonProperty("lastModYmd") String lastModifiedDate,
        @JsonProperty("servId") String serviceId,
        @JsonProperty("servNm") String serviceName,
        @JsonProperty("trgterIndvdlNmArray") String target,
        @JsonProperty("bizChrDeptNm") String department,
        @JsonProperty("ctpvNm") String sidoName,
        @JsonProperty("sggNm") String sigunguName
) { }
