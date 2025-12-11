package team.java.facto_be.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocalWelfareDetailResponse(
        @JsonProperty("resultCode") String resultCode,
        @JsonProperty("resultMessage") String resultMessage,
        @JsonProperty("servId") String servId,
        @JsonProperty("servNm") String servNm,
        @JsonProperty("enfcBgngYmd") String enforcementStartDate,
        @JsonProperty("enfcEndYmd") String enforcementEndDate,
        @JsonProperty("bizChrDeptNm") String departmentName,
        @JsonProperty("ctpvNm") String ctpvNm,
        @JsonProperty("sggNm") String sggNm,
        @JsonProperty("servDgst") String servDgst,
        @JsonProperty("lifeNmArray") String lifeNmArray,
        @JsonProperty("trgterIndvdlNmArray") String targetNameArray,
        @JsonProperty("intrsThemaNmArray") String interestThemeArray,
        @JsonProperty("sprtCycNm") String supportCycleName,
        @JsonProperty("srvPvsnNm") String providerName,
        @JsonProperty("aplyMtdNm") String applyMethodName,
        @JsonProperty("sprtTrgtCn") String supportTargetContent,
        @JsonProperty("slctCritCn") String selectionCriteriaContent,
        @JsonProperty("alwServCn") String allowanceServiceContent,
        @JsonProperty("aplyMtdCn") String applyMethodContent,
        @JsonProperty("inqNum") String inquiryCount,
        @JsonProperty("lastModYmd") String lastModifiedDate,
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        @JsonProperty("inqplCtadrList") List<LocalWelfareRelatedInfo> contactList,
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        @JsonProperty("inqplHmpgReldList") List<LocalWelfareRelatedInfo> homepageList,
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        @JsonProperty("baslawList") List<LocalWelfareRelatedInfo> basicLawList,
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        @JsonProperty("basfrmList") List<LocalWelfareRelatedInfo> basicFormList
) { }
