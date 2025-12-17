package com.truthify.domain.ad;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.truthify.domain.UserFeedback;

@Mapper
public interface FeedbackMapper {

	@Insert("""
			    INSERT INTO userFeedback (adTextId, userId, trustScore, regDate)
			    VALUES (#{adTextId}, #{member.id}, #{trustScore}, NOW())
			""")
	void save(UserFeedback feedback);

	@Select("""
			    SELECT COUNT(*)
			    FROM userFeedback
			    WHERE userId = #{userId}
			    AND adTextId = #{adTextId}
			""")
	int exists(@Param("userId") Long userId, @Param("adTextId") Long adTextId);

	@Select("""
			 SELECT
			  COALESCE(SUM(CASE WHEN trustScore = 1 THEN 1 ELSE 0 END),0) AS trustCount,
			  COALESCE(SUM(CASE WHEN trustScore = 0 THEN 1 ELSE 0 END),0) AS distrustCount,
			  COUNT(*) AS total
			FROM userFeedback
			WHERE adTextId = #{adTextId}

			    """)
	FeedbackStats findStatsByAdId(Long adTextId);

	@Select("""
			    SELECT trustScore
			    FROM userFeedback
			    WHERE userId = #{userId}
			    AND adTextId = #{adTextId}
			""")
	Integer findMyVote(@Param("userId") Long userId, @Param("adTextId") Long adTextId);

	@Update("""
			UPDATE userFeedback
				SET trustScore = #{trustScore}, regDate = NOW()
				WHERE userId = #{userId}
				AND adTextId = #{adTextId}
			""")
	void update(@Param("userId") Long userId, @Param("adTextId") Long adTextId, @Param("trustScore") int trustScore);

	@Delete("""
			DELETE FROM userFeedback
			WHERE userId = #{userId}
			AND adTextId = #{adTextId}
			""")
	void delete(@Param("userId") Long userId, @Param("adTextId") Long adTextId);
}
