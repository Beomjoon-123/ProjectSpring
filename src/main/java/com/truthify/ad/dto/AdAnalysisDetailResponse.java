package com.truthify.ad.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdAnalysisDetailResponse {

    private Long adTextId;
    private String adText;          // 광고 원문
    private Double riskScore;
    private Integer exgCount;
    private Integer banWordCount;
    private String summary;
    private List<HighRiskSentence> highRiskSentences;
    private Boolean isPublic;
    private Boolean isOwner;
    private List<String> matchedBanWords;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HighRiskSentence {
        private String sentence;
        private String reason;
    }
} 