package team.java.facto_be.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocalWelfareItem(
        @JacksonXmlProperty(localName = "servDgst") String summary,
        @JacksonXmlProperty(localName = "servDtlLink") String detailLink,
        @JacksonXmlProperty(localName = "lifeNmArray") String lifeCycle,
        @JacksonXmlProperty(localName = "intrsThemaNmArray") String interestTheme,
        @JacksonXmlProperty(localName = "sprtCycNm") String supportCycle,
        @JacksonXmlProperty(localName = "srvPvsnNm") String provider,
        @JacksonXmlProperty(localName = "aplyMtdNm") String applyMethod,
        @JacksonXmlProperty(localName = "inqNum") String inquiryCount,
        @JacksonXmlProperty(localName = "lastModYmd") String lastModifiedDate,
        @JacksonXmlProperty(localName = "servId") String serviceId,
        @JacksonXmlProperty(localName = "servNm") String serviceName,
        @JacksonXmlProperty(localName = "trgterIndvdlNmArray") String target,
        @JacksonXmlProperty(localName = "bizChrDeptNm") String department,
        @JacksonXmlProperty(localName = "ctpvNm") String sidoName,
        @JacksonXmlProperty(localName = "sggNm") String sigunguName
) { }
