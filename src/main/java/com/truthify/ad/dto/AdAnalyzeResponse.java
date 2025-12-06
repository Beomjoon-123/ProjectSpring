package com.truthify.ad.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 광고 분석 요청에 대한 응답 DTO 분석 결과의 요약 정보 및 ID를 담습니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdAnalyzeResponse {
	private Long adTextId; // 저장된 광고 텍스트 ID (PK)
	private Integer riskScore; // AI가 판단한 위험 점수 (0-100)
	private String summary; // 분석 결과 요약 설명
	private String detailDescription;
	// 추후 상세 분석 결과 필드 (e.g., exaggerationCount, highRiskSentences) 추가 예정

}