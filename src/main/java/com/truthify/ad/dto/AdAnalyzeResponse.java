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
	private Long adTextId;
	private Double riskScore; 
	private String summary; 
	private String detailDescription;
}