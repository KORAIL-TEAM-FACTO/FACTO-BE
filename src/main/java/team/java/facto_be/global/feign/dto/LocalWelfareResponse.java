package team.java.facto_be.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocalWelfareResponse(
        @JsonProperty("resultCode") String resultCode,
        @JsonProperty("resultMessage") String resultMessage,
        @JsonProperty("numOfRows") String numOfRows,
        @JsonProperty("pageNo") String pageNo,
        @JsonProperty("totalCount") String totalCount,
        @JsonProperty("servList") List<LocalWelfareItem> servList
) { }
