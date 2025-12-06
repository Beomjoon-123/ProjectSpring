package com.truthify.service;

import java.util.List;
import java.util.Optional; // 💡 Optional Import 추가
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.truthify.domain.user.Member; 
import com.truthify.domain.user.MemberMapper; 
import com.truthify.user.dto.UserRequest;
import com.truthify.user.dto.UserResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 ID를 기준으로 회원 정보 조회.
     * memberMapper.findByLoginId가 Optional<Member>를 반환하므로 이에 맞춰 수정
     */
	public UserResponse getMemberByLoginId(String loginId) {
        // 💡 수정: Optional<Member>를 반환받습니다.
		Optional<Member> memberOptional = memberMapper.findByLoginId(loginId); 
        
        // Optional이 비어있으면 (회원이 없으면) null 반환
		if (memberOptional.isEmpty()) { 
			return null;
		}
        // Optional에서 Member 객체를 꺼내서 응답 DTO로 변환
		return UserResponse.fromEntity(memberOptional.get()); 
	}

    /**
     * 회원 고유 ID를 기준으로 회원 정보 조회 (ID는 Long 타입)
     */
	public UserResponse getMemberById(Long id) {
		Member member = memberMapper.findById(id); 
        
		if (member == null) { 
			throw new IllegalArgumentException("존재하지 않는 회원입니다");
		}
		return UserResponse.fromEntity(member); 
	}
    
    /**
     * 회원가입 시 로그인 ID 중복 여부를 확인합니다.
     * @param loginId 확인할 로그인 ID
     * @return true (중복임), false (중복 아님)
     */
    public boolean checkLoginIdDuplication(String loginId) {
        // 💡 Optional의 isPresent() 메서드를 사용하여 존재 여부 확인
        return memberMapper.findByLoginId(loginId).isPresent();
    }


	public List<UserResponse> getAllUsers() {
		return memberMapper.findAll().stream().map(UserResponse::fromEntity).collect(Collectors.toList()); 
	}

	@Transactional
	public UserResponse joinMember (UserRequest request) {
        // 💡 수정: Optional<Member>를 반환받습니다.
        Optional<Member> existingMemberOptional = memberMapper.findByLoginId(request.getLoginId());
        
        // 💡 수정: isPresent()를 사용하여 이미 존재하는지 확인합니다.
		if (existingMemberOptional.isPresent()) {
			throw new IllegalArgumentException("이미 존재하는 아이디입니다");
		}
		
        // Member.builder() 사용
		Member member = Member.builder()
            .loginId(request.getLoginId())
            .loginPw(passwordEncoder.encode(request.getLoginPw())) 
            .email(request.getEmail())
            .nickname(request.getNickname())
            .role(com.truthify.domain.user.Role.USER) 
			.build();
		
		memberMapper.save(member);
		
		return UserResponse.fromEntity(member);
	}
	
	@Transactional
	public UserResponse updateUser(Long id, UserRequest request) {
		Member member = memberMapper.findById(id);
		if (member == null) {
			throw new IllegalArgumentException("찾을수 없는 유저입니다");
		}
        
		String encodedPw = null;
		if (request.getLoginPw() != null && !request.getLoginPw().isEmpty()) {
			encodedPw = passwordEncoder.encode(request.getLoginPw());
		}
		
		Member memberToUpdate = Member.builder()
            .id(id) // ID는 Long 타입
            .nickname(request.getNickname())
            .loginPw(encodedPw)
            .build();
		
		memberMapper.modifyMember(memberToUpdate); 
		
		return getMemberById(id);
	}
	
	public boolean checkNicknameDuplication(String nickname) {
		return memberMapper.findByNickname(nickname).isPresent();
	}
	
	@Transactional
	// ID 타입을 Long으로 변경
	public void deleteUser(Long id) {
		memberMapper.delete(id);
	}
}