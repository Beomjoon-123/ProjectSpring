package com.truthify.domain.ad;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.truthify.domain.AnalysisResult;
import java.util.Optional;

//AI 분석 결과 (analResult 테이블) 데이터베이스 접근을 위한 MyBatis Mapper 인터페이스

@Mapper
public interface AnalysisResultMapper {

	// AI 분석 결과를 DB에 저장합니다.

	@Insert("""
			    INSERT INTO analResult (
			        adTextId, exgCount, banWordCount, highRiskSentences, summary, regDate
			    ) VALUES (
			        #{adTextId}, #{exgCount}, #{banWordCount}, #{highRiskSentences}, #{summary}, NOW()
			    )
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void save(AnalysisResult analysisResult);

	/**
	 * adTextId를 기준으로 분석 결과 조회 Feedback을 저장하거나, 상세 분석 정보 가져올 때 사용
	 */
	@Select("""
			    SELECT
			        id, adTextId, exgCount, banWordCount, highRiskSentences, summary, regDate
			    FROM analResult
			    WHERE adTextId = #{adTextId}
			""")
	Optional<AnalysisResult> findByAdTextId(@Param("adTextId") Integer adTextId);

	// 필요한 경우 업데이트 메서드 (update)도 추가될 수 있습니다.
}