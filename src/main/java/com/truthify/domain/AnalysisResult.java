package com.truthify.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {
    private Long id; // PK
    private Long adTextId; // FK (광고 텍스트 ID)
    private Integer exgCount;       // 과장 문구 개수
    private Integer banWordCount;    // 금지어 개수
    private Double riskScore;        // AI가 계산한 위험 점수
    private String highRiskSentences;// JSON 문자열로 고위험 문장
    private String summary;          // AI 분석 요약
    private LocalDateTime regDate;   // 등록일
}
