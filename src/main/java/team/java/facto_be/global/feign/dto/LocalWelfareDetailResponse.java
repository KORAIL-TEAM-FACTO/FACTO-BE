package team.java.facto_be.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 지자체 복지 서비스 상세 조회 응답 DTO.
 */
@Getter
@NoArgsConstructor
public class LocalWelfareDetailResponse {

    @JsonProperty("resultCode")
    private String resultCode;

    @JsonProperty("resultMessage")
    private String resultMessage;

    @JsonProperty("servId")
    private String servId;

    @JsonProperty("servNm")
    private String servNm;

    @JsonProperty("enfcBgngYmd")
    private String enfcBgngYmd;

    @JsonProperty("enfcEndYmd")
    private String enfcEndYmd;

    @JsonProperty("bizChrDeptNm")
    private String bizChrDeptNm;

    @JsonProperty("ctpvNm")
    private String ctpvNm;

    @JsonProperty("sggNm")
    private String sggNm;

    @JsonProperty("servDgst")
    private String servDgst;

    @JsonProperty("lifeNmArray")
    private String lifeNmArray;

    @JsonProperty("trgterIndvdlNmArray")
    private String trgterIndvdlNmArray;

    @JsonProperty("intrsThemaNmArray")
    private String intrsThemaNmArray;

    @JsonProperty("sprtCycNm")
    private String sprtCycNm;

    @JsonProperty("srvPvsnNm")
    private String srvPvsnNm;

    @JsonProperty("aplyMtdNm")
    private String aplyMtdNm;

    @JsonProperty("sprtTrgtCn")
    private String sprtTrgtCn;

    @JsonProperty("slctCritCn")
    private String slctCritCn;

    @JsonProperty("alwServCn")
    private String alwServCn;

    @JsonProperty("aplyMtdCn")
    private String aplyMtdCn;

    @JsonProperty("inqNum")
    private String inqNum;

    @JsonProperty("lastModYmd")
    private String lastModYmd;

    @JsonProperty("inqplCtadrList")
    private WelfareInfoReld inqplCtadrList;

    @JsonProperty("inqplHmpgReldList")
    private List<WelfareInfoReld> inqplHmpgReldList;

    @Getter
    @NoArgsConstructor
    public static class WelfareInfoReld {
        @JsonProperty("wlfareInfoReldNm")
        private String wlfareInfoReldNm;

        @JsonProperty("wlfareInfoReldCn")
        private String wlfareInfoReldCn;

        @JsonProperty("wlfareInfoDtlCd")
        private String wlfareInfoDtlCd;
    }
}
