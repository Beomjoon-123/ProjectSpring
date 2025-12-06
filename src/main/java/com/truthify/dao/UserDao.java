package com.truthify.dao;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.truthify.domain.User;

@Mapper
public interface UserDao {

	// loginId로 멤버 조회
	@Select("""
			    SELECT id, loginId, loginPw, nickname, email, picture, provider, providerId, auth_level AS authLevel,
			           regDate, updateDate
			      FROM `member`
			     WHERE loginId = #{loginId}
			""")
	User getMemberByLoginId(String loginId);

	// id로 멤버 조회
	@Select("""
			    SELECT id, loginId, loginPw, nickname, email, picture, provider, providerId, auth_level AS authLevel,
			           regDate, updateDate
			      FROM `member`
			     WHERE id = #{id}
			""")
	User getMemberById(int id);

	// 회원가입
	@Insert("""
			    INSERT INTO `member` (loginId, loginPw, nickname, email, auth_level, regDate, updateDate)
			    VALUES (#{loginId}, #{loginPw}, #{nickname}, #{email}, #{authLevel}, NOW(), NOW())
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void joinMember(User user);

	// 회원 수정
	@Update("""
			    <script>
			    UPDATE `member`
			       SET updateDate = NOW(),
			           nickname = #{nickname}
			       <if test="loginPw != null and loginPw != ''">
			           , loginPw = #{loginPw}
			       </if>
			     WHERE id = #{id}
			    </script>
			""")
	void modifyMember(User user);

	// 회원 삭제
	@Delete("""
			    DELETE FROM `member`
			     WHERE id = #{id}
			""")
	void deleteMember(int id);

	// 전체 회원 조회
	@Select("""
			    SELECT id, loginId, loginPw, nickname, email, picture, provider, providerId, auth_level AS authLevel,
			           regDate, updateDate
			      FROM `member`
			     ORDER BY id DESC
			""")
	List<User> getAllMembers();
}
