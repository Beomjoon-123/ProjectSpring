package com.truthify.domain.ad;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FeedbackMapper {

	/**
	 * 사용자의 신뢰도 피드백 DB에 저장 adTextId와 userId의 UNIQUE KEY 제약 조건
	 */
	@Insert("""
			    INSERT INTO Feedback (
			        adTextId, userId, trustScore, regDate
			    ) VALUES (
			        #{adTextId}, #{userId}, #{trustScore}, NOW()
			    )
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void save(@Param("adTextId") Integer adTextId, @Param("userId") Integer userId,
			@Param("trustScore") boolean trustScore);

	// 추가적인 조회 메서드 (예: findByAdTextId)는 필요에 따라 추가
}