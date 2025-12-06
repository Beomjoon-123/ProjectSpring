package com.truthify.ad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 광고 분석 결과에 대한 사용자 피드백을 저장하기 위한 요청 DTO 사용자가 분석 결과를 신뢰하는지 여부를 담습니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdFeedbackRequest {
	private Integer adTextId; // 피드백을 남길 광고 텍스트 ID (PK)
	private Boolean trustScore; // true: 신뢰함, false: 신뢰 안 함
}