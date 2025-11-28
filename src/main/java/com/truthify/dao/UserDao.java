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
	@Select("""
			SELECT *
				FROM `user`
				WHERE loginId = #{loginId}
			""")
	User getMemberByLoginId(String loginId);
	
	@Select("""
			SELECT *
				FROM `user`
				WHERE id = #{id}
			""")
	User getMemberById(int id);
	
	@Insert("""
			INSERT INTO `user`
				SET regDate = NOW()
					, updateDate = NOW()
					, loginId = #{loginId}
					, loginPw = #{loginPw}
					, email = #{email}
					, nickname = #{nickname}
					, role = #{role}
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void joinMember(User user);
	
	@Update("""
			UPDATE `user`
				SET updateDate = now()
					, nickname = #{nickname}
					<if test="loginPw != null and loginPw != ''">
						, loginPw = #{loginPw}
					</if>
					WHERE id = #{id}
			""")
	void modifyMember(User user);
	
	@Delete("""
			DELETE FROM `user`
			WHERE id = #{id}
			""")
	void deleteMember(int id);
	
	@Select("""
			SELECT *
				FROM `user`
				ORDER BY id DESC
			""")
	List<User> getAllMembers();
}
