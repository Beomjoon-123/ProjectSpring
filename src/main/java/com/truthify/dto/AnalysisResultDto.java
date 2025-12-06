package com.truthify.dto;

import lombok.Data;

/**
 * 외부 AI 분석 서버로부터 받는 응답 DTO
 */
@Data
public class AnalysisResultDto {

	private boolean success; // 분석 성공 여부
	private String entities; // 고위험 문장, 키워드 등 (JSON 문자열 또는 쉼표 구분 문자열)
	private String detailDescription; // AI가 제공하는 상세 요약 의견
	private String sentiment; // 텍스트 감정 분석 결과 (임시로 Risk Score 계산에 사용됨)

	// AdService의 주석 해제 로직에서 참조하는 필드 추가
	private Integer exgCount; // 과장/허위 표현 개수
	private Integer banWordCount; // 금지어 개수

	/**
	 * 분석 성공 여부를 확인하는 편의 메서드
	 * 
	 * @return 성공 시 true
	 */
	public boolean isSuccess() {
		return success;
	}
}