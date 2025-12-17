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
                id, loginId, loginPw, nickname, email,
                provider, providerId, role,
                regDate, updateDate, name, phone
            FROM member
            WHERE email = #{email}
            """)
    Optional<Member> findByEmail(@Param("email") String email);


    @Insert("""
            INSERT INTO member (
                loginId, loginPw, nickname, email,
                role, provider, providerId,
                regDate, updateDate, name, phone
            ) VALUES (
                #{loginId},
                #{loginPw},
                #{nickname},
                #{email, jdbcType=VARCHAR},
                #{role, typeHandler=com.truthify.config.handler.RoleTypeHandler},
                #{provider},
                #{providerId},
                NOW(),
                NOW(),
                #{name},
                #{phone}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(Member member);


    @Update("""
            UPDATE member
            SET nickname = #{nickname},
                updateDate = NOW()
            WHERE id = #{id}
            """)
    void update(Member member);


    // ----------------------------------------------------
    // 2. 일반 회원 관리
    // ----------------------------------------------------

    @Select("""
            SELECT
                id, loginId, loginPw, nickname, email,
                provider, providerId, role,
                regDate, updateDate, name, phone
            FROM member
            WHERE loginId = #{loginId}
            """)
    Optional<Member> findByLoginId(@Param("loginId") String loginId);


    @Select("""
            SELECT
                id, loginId, loginPw, nickname, email,
                provider, providerId, role,
                regDate, updateDate, name, phone
            FROM member
            WHERE id = #{id}
            """)
    Member findById(@Param("id") Long id);


    @Select("""
            SELECT
                id, loginId, loginPw, nickname, email,
                provider, providerId, role,
                regDate, updateDate, name, phone
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


    @Update("""
            UPDATE member
            SET loginPw = #{newPassword},
                updateDate = NOW()
            WHERE id = #{memberId}
            """)
    void modifyPassword(@Param("memberId") Long memberId, @Param("newPassword") String newPassword);


    @Select("""
            SELECT *
            FROM member
            WHERE nickname = #{nickname}
            """)
    Optional<Member> findByNickname(@Param("nickname") String nickname);


    @Delete("DELETE FROM member WHERE id = #{id}")
    void delete(@Param("id") Long id);


    @Select("""
            SELECT loginId
            FROM member
            WHERE email = #{email}
            """)
    String findLoginIdByEmail(@Param("email") String email);


    @Select("""
            SELECT *
            FROM member
            WHERE loginId = #{loginId}
            AND email = #{email}
            """)
    Optional<Member> findByLoginIdAndEmail(
            @Param("loginId") String loginId,
            @Param("email") String email
    );
    
 // 소셜 로그인용
    @Select("""
        SELECT *
        FROM member
        WHERE provider = #{provider} AND providerId = #{providerId}
    """)
    Optional<Member> findByProviderAndProviderId(@Param("provider") String provider,
                                                 @Param("providerId") String providerId);

}