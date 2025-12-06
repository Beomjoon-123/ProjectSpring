package com.truthify.domain.user;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {

	// ----------------------------------------------------
	// 1. 소셜 로그인 관련
	// ----------------------------------------------------

	@Select("""
			    SELECT
			        id, loginId, loginPw, nickname, email, picture,
			        provider, providerId, auth_level AS role,
			        regDate, updateDate
			    FROM member
			    WHERE email = #{email}
			""")
	Optional<Member> findByEmail(@Param("email") String email);

	@Insert("""
			    INSERT INTO member (
			        loginId, loginPw, nickname, email, picture, provider, providerId, auth_level, regDate, updateDate
			    ) VALUES (
			        #{loginId},
			        #{loginPw},
			        #{nickname},
			        #{email},
			        #{picture},
			        #{provider},
			        #{providerId},
			        #{role, typeHandler=com.truthify.config.handler.RoleTypeHandler},
			        NOW(),
			        NOW()
			    )
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void save(Member member);

	@Update("""
			    UPDATE member
			    SET
			        nickname = #{nickname},
			        picture = #{picture},
			        updateDate = NOW()
			    WHERE id = #{id}
			""")
	void update(Member member);

	// ----------------------------------------------------
	// 2. 일반 회원 관리
	// ----------------------------------------------------

	@Select("""
			    SELECT
			        id, loginId, loginPw, nickname, email, picture,
			        provider, providerId, auth_level AS role,
			        regDate, updateDate
			    FROM member
			    WHERE loginId = #{loginId}
			""")
	Optional<Member> findByLoginId(@Param("loginId") String loginId);

	@Select("""
			    SELECT
			        id, loginId, loginPw, nickname, email, picture,
			        provider, providerId, auth_level AS role,
			        regDate, updateDate
			    FROM member
			    WHERE id = #{id}
			""")
	Member findById(@Param("id") Long id);

	@Select("""
			    SELECT
			        id, loginId, loginPw, nickname, email, picture,
			        provider, providerId, auth_level AS role,
			        regDate, updateDate
			    FROM member
			    ORDER BY id DESC
			""")
	List<Member> findAll();

	@Update("""
			    UPDATE member
			    SET nickname = #{nickname},
			        loginPw = COALESCE(#{loginPw}, loginPw),
			        updateDate = NOW()
			    WHERE id = #{id}
			""")
	void modifyMember(Member member);

	@Select("""
			SELECT *
				FROM `member`
				WHERE nickname = #{nickname}
			""")
	Optional<Member> findByNickname(@Param("nickname") String nickname);

	@Delete("DELETE FROM member WHERE id = #{id}")
	void delete(@Param("id") Long id);
}
