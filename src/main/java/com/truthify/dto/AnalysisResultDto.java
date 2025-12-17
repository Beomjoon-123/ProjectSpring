package com.truthify.dto;

import lombok.Data;

@Data
public class AnalysisResultDto {
    private boolean success;              // 분석 성공 여부
    private String entities;              // 고위험 문장 JSON 문자열
    private String detailDescription;     // AI 상세 요약 의견
    private String sentiment;             // 감정 분석 → 위험 점수 계산에 활용 가능
    private Integer exgCount;             // 과장 문구 개수
    private Integer banWordCount;         // 금지어 개수            // AI 점수
}
