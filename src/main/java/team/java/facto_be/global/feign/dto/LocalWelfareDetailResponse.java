package team.java.facto_be.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "response")
public record LocalWelfareDetailResponse(
        @JacksonXmlProperty(localName = "header") Header header,
        @JacksonXmlProperty(localName = "body") Body body
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Header(
            @JacksonXmlProperty(localName = "resultCode") String resultCode,
            @JacksonXmlProperty(localName = "resultMsg") String resultMsg
    ) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(
            @JacksonXmlProperty(localName = "servId") String servId,
            @JacksonXmlProperty(localName = "servNm") String servNm,
            @JacksonXmlProperty(localName = "enfcBgngYmd") String enforcementStartDate,
            @JacksonXmlProperty(localName = "enfcEndYmd") String enforcementEndDate,
            @JacksonXmlProperty(localName = "bizChrDeptNm") String departmentName,
            @JacksonXmlProperty(localName = "ctpvNm") String ctpvNm,
            @JacksonXmlProperty(localName = "sggNm") String sggNm,
            @JacksonXmlProperty(localName = "servDgst") String servDgst,
            @JacksonXmlProperty(localName = "lifeNmArray") String lifeNmArray,
            @JacksonXmlProperty(localName = "trgterIndvdlNmArray") String targetNameArray,
            @JacksonXmlProperty(localName = "intrsThemaNmArray") String interestThemeArray,
            @JacksonXmlProperty(localName = "sprtCycNm") String supportCycleName,
            @JacksonXmlProperty(localName = "srvPvsnNm") String providerName,
            @JacksonXmlProperty(localName = "aplyMtdNm") String applyMethodName,
            @JacksonXmlProperty(localName = "sprtTrgtCn") String supportTargetContent,
            @JacksonXmlProperty(localName = "slctCritCn") String selectionCriteriaContent,
            @JacksonXmlProperty(localName = "alwServCn") String allowanceServiceContent,
            @JacksonXmlProperty(localName = "aplyMtdCn") String applyMethodContent,
            @JacksonXmlProperty(localName = "inqNum") String inquiryCount,
            @JacksonXmlProperty(localName = "lastModYmd") String lastModifiedDate,
            @JacksonXmlProperty(localName = "inqplCtadrList") ListWrapper<LocalWelfareRelatedInfo> contactList,
            @JacksonXmlProperty(localName = "inqplHmpgReldList") ListWrapper<LocalWelfareRelatedInfo> homepageList,
            @JacksonXmlProperty(localName = "baslawList") ListWrapper<LocalWelfareRelatedInfo> basicLawList,
            @JacksonXmlProperty(localName = "basfrmList") ListWrapper<LocalWelfareRelatedInfo> basicFormList
    ) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ListWrapper<T>(
            @JacksonXmlProperty(localName = "item") java.util.List<T> item
    ) { }
}
