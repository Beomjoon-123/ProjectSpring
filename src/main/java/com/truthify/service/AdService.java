package com.truthify.service;

import com.truthify.config.auth.dto.SessionUser;
import com.truthify.dao.AdTextMapper;	
import com.truthify.domain.AdText;
import com.truthify.domain.AnalysisResult;
import com.truthify.domain.User;
import com.truthify.domain.UserFeedback;
import com.truthify.dto.AnalysisResultDto;
import com.truthify.ad.dto.AdAnalyzeRequest; // 수정된 DTO 경로
import com.truthify.ad.dto.AdAnalyzeResponse; // 수정된 DTO 경로
import com.truthify.ad.dto.AdFeedbackRequest; // 임시로 AdFeedbackRequest도 dto 패키지로 가정
import com.truthify.domain.user.Member;
import com.truthify.domain.user.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;	
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdService {

	private final WebClient webClient;
	private final AdTextMapper adTextMapper;	
	private final MemberMapper memberMapper;

	/**
	 * 광고 분석 요청을 AI 서버에 전송하고, 결과를 DB에 저장합니다.
	 * @param request 광고 텍스트 내용 (AdAnalyzeRequest로 변경)
	 * @param principal 현재 로그인 사용자 정보 (SessionUser)
	 * @return AdAnalyzeResponse (분석 결과 요약)
	 */
	@Transactional
	public AdAnalyzeResponse analyzeAndSaveAdText(AdAnalyzeRequest request, SessionUser principal) {
		
		// 1. 로그인 사용자 (Member) 정보 조회
		Optional<Member> memberOptional = memberMapper.findByEmail(principal.getEmail());
		if (!memberOptional.isPresent()) {
			throw new IllegalArgumentException("로그인된 사용자 정보를 DB에서 찾을 수 없습니다.");
		}
		Member member = memberOptional.get();

		// 2. AI 서버에 분석 요청 (WebClient 사용)
		// AI 서버 URL은 application.yml의 truthify.ai.url에 설정되어 있음
		AnalysisResultDto aiResponse = webClient.post()
				.uri("/analyze")	
				.bodyValue(request)
				.retrieve()
				.bodyToMono(AnalysisResultDto.class)
				.block();	
		
		if (aiResponse == null || !aiResponse.isSuccess()) {
			 throw new RuntimeException("AI 분석 서버 응답 오류 또는 분석 실패");
		}

		// 3. AdText 엔티티 생성 및 저장
		User userForAdText = User.builder().id(member.getId()).build(); // Long 타입 사용
		
		// AI 응답의 sentiment를 이용하여 riskScore 설정 (임시 로직)
		int riskScore = aiResponse.getSentiment() != null ? 50 : 0;
		if (aiResponse.getExgCount() > 0 || aiResponse.getBanWordCount() > 0) {
		    riskScore = Math.min(100, riskScore + 25 * (aiResponse.getExgCount() + aiResponse.getBanWordCount()));
		}
		
		AdText adText = AdText.builder()
				.user(userForAdText)	
				.textContent(request.getAdContent())
				.riskScore(riskScore)
				.build();
		
		// adTextMapper.saveAdText(adText) 호출을 통해 ID가 설정된다고 가정
		adTextMapper.saveAdText(adText);	

		// 4. AnalysisResult 엔티티 생성 및 저장
		AnalysisResult analysisResult = AnalysisResult.builder()
				.adTextId(adText.getId())	
				.exgCount(aiResponse.getExgCount() != null ? aiResponse.getExgCount() : 0)
				.banWordCount(aiResponse.getBanWordCount() != null ? aiResponse.getBanWordCount() : 0)
				.highRiskSentences(aiResponse.getEntities()) // AI가 지적한 고위험 문장들
				.summary(aiResponse.getDetailDescription()) // AI 분석 요약/상세 내용을 DB summary 컬럼에 저장
				.build();

		adTextMapper.saveAnalysisResult(analysisResult);
		
		// 5. 응답 DTO 구성
		AdAnalyzeResponse response = new AdAnalyzeResponse();
		
		// 수정: int를 long으로 캐스팅하여 setAdTextId(Long)에 전달 (Autoboxing 활용)
		response.setAdTextId((long) adText.getId());	
		
		response.setRiskScore(adText.getRiskScore());	
		
		// summary: DB에 저장된 분석 요약 (AnalysisResult.summary)
		response.setSummary(analysisResult.getSummary());	
		
		// detailDescription: AnalysisResult 엔티티의 summary 필드에 detailDescription을 저장하고 있으므로, 일관성을 위해 DB 값을 사용합니다.
		response.setDetailDescription(analysisResult.getSummary());
		
		return response;
	}
	
	/**
	 * 사용자 피드백을 저장합니다.
	 * @param request 피드백 요청 DTO
	 * @param principal 현재 로그인 사용자 정보
	 */
	@Transactional
	// AdFeedbackRequest가 com.truthify.ad.dto에 없으므로 com.truthify.dto에 있다고 임시 가정
	public void submitFeedback(AdFeedbackRequest request, SessionUser principal) {
		
		Optional<Member> memberOptional = memberMapper.findByEmail(principal.getEmail());
		if (!memberOptional.isPresent()) {
			throw new IllegalArgumentException("로그인된 사용자 정보를 DB에서 찾을 수 없습니다.");
		}
		Member member = memberOptional.get();
		
		// 1. AdText 조회
		AdText adText = adTextMapper.getAdTextById(request.getAdTextId());
		if (adText == null) {
			 throw new IllegalArgumentException("존재하지 않는 광고 분석 ID입니다.");
		}
		
		// 2. UserFeedback 생성 및 저장
		User userForFeedback = User.builder().id(member.getId()).build();	

		UserFeedback feedback = UserFeedback.builder()
				.adText(adText)
				.user(userForFeedback)
				.trustScore(request.getTrustScore())
				.build();
				
		adTextMapper.saveUserFeedback(feedback);
	}
}