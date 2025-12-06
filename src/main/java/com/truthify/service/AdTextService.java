package com.truthify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.truthify.dto.AdTextRequest;
import com.truthify.dto.AdTextResponse;

@Service
@RequiredArgsConstructor
public class AdTextService {

	private final WebClient webClient;

	/**
	 * 광고 문장 분석 요청
	 */
	public AdTextResponse analyzeAdText(AdTextRequest request) {

		// 아직 AI 모델 연동 안 할 거니까, 임시 로직으로 구성
		String text = request.getText();

		// 예시 응답 (임시)
		AdTextResponse response = new AdTextResponse();
		response.setOriginalText(text);
		response.setIsTrue(text.length() % 2 == 0); // 임시 판단
		response.setConfidenceScore(75.5);
		response.setMessage("AI 모델 연동 전 임시 결과입니다.");

		return response;
	}
}
