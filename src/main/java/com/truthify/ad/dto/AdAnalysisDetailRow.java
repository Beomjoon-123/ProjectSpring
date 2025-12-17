package com.truthify.ad.dto;

import lombok.Data;

@Data
public class AdAnalysisDetailRow {

    private Long adTextId;
    private Long userId;
    private Boolean isPublic;
    private String content;              
    private Double riskScore;
    private Integer exgCount;
    private Integer banWordCount;
    private String summary;
    private String highRiskSentences; 
}