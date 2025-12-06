package com.truthify.ad.dto;

import lombok.Data;

/**
 * 광고 분석을 위한 요청 DTO 사용자가 입력한 광고 텍스트 내용을 담습니다.
 */
@Data
public class AdAnalyzeRequest {
	private String adContent; // 사용자가 입력한 광고 텍스트 내용
}