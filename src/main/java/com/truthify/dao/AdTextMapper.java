package com.truthify.dao;

import com.truthify.ad.dto.AdAnalysisDetailRow;
import com.truthify.ad.dto.MyAdHistoryItem;
import com.truthify.ad.dto.PublicAdItem;
import com.truthify.domain.AdText;
import com.truthify.domain.AnalysisResult;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 광고 텍스트 및 분석 결과를 DB에 저장/조회하는 MyBatis Mapper 인터페이스
 */
@Mapper
public interface AdTextMapper {

	@Insert("""
			    INSERT INTO adText (userId, content, riskScore, isPublic ,regDate)
			    VALUES (#{member.id}, #{textContent}, #{riskScore}, false, NOW())
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void saveAdText(AdText adText);

	@Insert("""
			INSERT INTO analResult(adTextId, exgCount, banWordCount, riskScore,highRiskSentences, summary, regDate)
				VALUES(#{adTextId}, #{exgCount}, #{banWordCount}, #{riskScore}, #{highRiskSentences}, #{summary}, NOW())
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void saveAnalysisResult(AnalysisResult analysisResult);

	@Select("""
			SELECT id AS adTextId, content, riskScore, regDate
			FROM adText
			WHERE userId = #{userId}
			ORDER BY regDate DESC
			""")
	List<MyAdHistoryItem> findMyHistoryByUserId(Long userId);

	@Select("""
				SELECT
			    at.id          AS adTextId,
			    at.userId      AS userId,
			    at.isPublic    AS isPublic,
			    at.content     AS content,
			    at.riskScore   AS riskScore,
			    ar.exgCount    AS exgCount,
			    ar.banWordCount AS banWordCount,
			    ar.summary     AS summary,
			    ar.highRiskSentences AS highRiskSentences
			FROM adText at
			JOIN analResult ar ON at.id = ar.adTextId
			WHERE at.id = #{adTextId}

						""")
	AdAnalysisDetailRow findAnalysisDetail(Long adTextId);

	@Update("""
			UPDATE adText
			SET isPublic = true
				, publicAt = NOW()
			WHERE id = #{adTextId} AND userId = #{userId}
			""")
	int makePublic(@Param("adTextId") Long adTextId, @Param("userId") Long userId);

	@Select("""
			SELECT *
				FROM analResult
				WHERE adTextId = #{adTextId}
			""")
	Optional<AnalysisResult> findAnalysisByAdId(Long adTextId);

	@Select("""
			    SELECT
			        at.id AS adTextId,
			        at.content,
			        at.riskScore,
			        at.publicAt,

			        SUM(CASE WHEN uf.trustScore = 1 THEN 1 ELSE 0 END) AS trustCount,
			        SUM(CASE WHEN uf.trustScore = 0 THEN 1 ELSE 0 END) AS distrustCount

			    FROM adText at
			    LEFT JOIN userFeedback uf ON uf.adTextId = at.id
			    WHERE at.isPublic = true
			    GROUP BY at.id
			    ORDER BY at.publicAt DESC
			""")
	List<PublicAdItem> findPublicAds();

	@Update("""
			UPDATE adText
				SET isPublic = #{isPublic}
					, publicAt = CASE WHEN #{isPublic} = true THEN NOW() ELSE NULL END
				WHERE id = #{adTextId} AND userId = #{userId}
			""")
	int updateVisibility(@Param("adTextId") Long adTextId, @Param("userId") Long userId,
			@Param("isPublic") boolean isPublic);

	@Select("""
			    SELECT COUNT(*)
			    FROM adText
			    WHERE isPublic = true
			""")
	long countPublicAds();

	@Select("""
			    SELECT
			        at.id AS adTextId,
			        at.content,
			        at.riskScore,
			        at.publicAt,
			        SUM(CASE WHEN uf.trustScore = 1 THEN 1 ELSE 0 END) AS trustCount,
			        SUM(CASE WHEN uf.trustScore = 0 THEN 1 ELSE 0 END) AS distrustCount
			    FROM adText at
			    LEFT JOIN userFeedback uf ON uf.adTextId = at.id
			    WHERE at.isPublic = true
			    GROUP BY at.id
			    ORDER BY at.publicAt DESC
			    LIMIT #{limit} OFFSET #{offset}
			""")
	List<PublicAdItem> findPublicAdsPage(@Param("limit") int limit, @Param("offset") int offset);

	@Select("""
			    SELECT at.content
			    FROM adText at
			    JOIN analResult ar ON ar.adTextId = at.id
			    WHERE ar.riskScore IS NOT NULL
			      AND ar.riskScore >= #{minRisk}
			    ORDER BY ar.riskScore DESC, ar.regDate DESC
			    LIMIT #{limit}
			""")
	List<String> findHighRiskSamples(@Param("minRisk") double minRisk, @Param("limit") int limit);
}