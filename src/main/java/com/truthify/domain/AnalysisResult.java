package com.truthify.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * AI 분석 결과 상세 정보를 담는 Entity/DTO DB의 anal_result 테이블과 매핑됩니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {
	private Integer id; // PK
	private Integer adTextId; // FK (광고 텍스트 ID)

	// AI 분석 결과 필드
	private Integer exgCount; // 과장 문구 카운트 (exaggeration_count)
	private Integer banWordCount; // 금지/주의어 카운트 (ban_word_count)
	private String highRiskSentences; // 고위험 문장 목록 (high_risk_sentences)
	private String summary; // AI 요약 의견 (summary_opinion)
	private LocalDateTime regDate;
}