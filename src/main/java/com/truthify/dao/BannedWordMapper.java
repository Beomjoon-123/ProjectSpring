package com.truthify.dao;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BannedWordMapper {

	@Select("""
			 SELECT id, keyword, lawType, description, isActive, regDate
			 FROM banned_words
			 ORDER BY id DESC
			""")
	List<com.truthify.domain.BannedWord> findAll();

	@Select("""
			 SELECT keyword
			 FROM banned_words
			 WHERE isActive = 1
			""")
	List<String> findActiveKeywords();

	@Insert("""
			 INSERT INTO banned_words(keyword, lawType, description, isActive, regDate)
			 VALUES(#{keyword}, #{lawType}, #{description}, #{isActive}, NOW())
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void insert(com.truthify.domain.BannedWord word);

	@Update("""
			 UPDATE banned_words
			 SET isActive = #{isActive}
			 WHERE id = #{id}
			""")
	int updateActive(@Param("id") Long id, @Param("isActive") boolean isActive);

	@Delete("DELETE FROM banned_words WHERE id = #{id}")
	int delete(@Param("id") Long id);
}
