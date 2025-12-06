package com.truthify.dao;

import com.truthify.domain.AdText;
import com.truthify.domain.AnalysisResult;
import com.truthify.domain.UserFeedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * 광고 텍스트 및 분석 결과를 DB에 저장/조회하는 MyBatis Mapper 인터페이스
 */
@Mapper
public interface AdTextMapper {

	/**
	 * 광고 텍스트(AdText) 정보를 DB에 저장합니다.
	 * 
	 * @param adText 저장할 AdText 객체
	 */
	void saveAdText(AdText adText); // <-- 이 메서드를 추가했습니다.

	/**
	 * 광고 분석 결과(AnalysisResult)를 DB에 저장합니다.
	 * 
	 * @param analysisResult 저장할 AnalysisResult 객체
	 */
	void saveAnalysisResult(AnalysisResult analysisResult);

	/**
	 * 특정 AdText ID로 광고 텍스트 정보를 조회합니다.
	 * 
	 * @param id 조회할 AdText ID
	 * @return AdText 객체
	 */
	AdText getAdTextById(Integer id);

	/**
	 * 사용자 피드백(UserFeedback)을 DB에 저장합니다.
	 * 
	 * @param userFeedback 저장할 UserFeedback 객체
	 */
	void saveUserFeedback(UserFeedback userFeedback);
}