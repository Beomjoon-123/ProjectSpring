package com.truthify.service;

import com.truthify.domain.user.Member;
import com.truthify.domain.user.MemberMapper;
import com.truthify.domain.user.Role;
import com.truthify.user.dto.UserRequest;
import com.truthify.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;

	public UserResponse getMemberByLoginId(String loginId) {
		return memberMapper.findByLoginId(loginId).map(UserResponse::fromEntity).orElse(null);
	}

	public UserResponse getMemberById(Long id) {
		Member member = memberMapper.findById(id);
		if (member == null)
			throw new IllegalArgumentException("존재하지 않는 회원입니다");
		return UserResponse.fromEntity(member);
	}

	public boolean checkLoginIdDuplication(String loginId) {
		return memberMapper.findByLoginId(loginId).isPresent();
	}

	public List<UserResponse> getAllUsers() {
		return memberMapper.findAll().stream().map(UserResponse::fromEntity).collect(Collectors.toList());
	}

	@Transactional
	public UserResponse joinMember(UserRequest request) {
		if (memberMapper.findByLoginId(request.getLoginId()).isPresent())
			throw new IllegalArgumentException("이미 존재하는 아이디입니다");

		Member member = Member.builder().loginId(request.getLoginId())
				.loginPw(passwordEncoder.encode(request.getLoginPw())).email(request.getEmail())
				.nickname(request.getNickname()).role(Role.USER).name(request.getName()).phone(request.getPhone()).build();

		memberMapper.save(member);
		return UserResponse.fromEntity(member);
	}

	@Transactional
	public UserResponse updateUser(Long id, UserRequest request) {
		Member member = memberMapper.findById(id);
		if (member == null)
			throw new IllegalArgumentException("찾을 수 없는 유저입니다");

		String encodedPw = Optional.ofNullable(request.getLoginPw())
				.filter(pw -> !pw.isEmpty())
				.map(passwordEncoder::encode)
				.orElse(null);

		Member memberToUpdate = Member.builder().id(id).nickname(request.getNickname()).loginPw(encodedPw).build();

		memberMapper.modifyMember(memberToUpdate);

		return getMemberById(id);
	}

	public boolean checkNicknameDuplication(String nickname) {
		return memberMapper.findByNickname(nickname).isPresent();
	}

	@Transactional
	public void deleteUser(Long id) {
		memberMapper.delete(id);
	}

	public String findLoginByEmail(String email) {
		return memberMapper.findLoginIdByEmail(email);
	}
	
	@Transactional
	public String sendTempPassword(String loginId, String email) {
		Optional<Member> optional = memberMapper.findByLoginIdAndEmail(loginId, email);

		if (optional.isEmpty())
			return null;

		Member member = optional.get();
		String tempPassword = generateRandomPassword();
		String encodedTempPassword = passwordEncoder.encode(tempPassword);
		memberMapper.modifyPassword(member.getId(), encodedTempPassword);
		
		return tempPassword;
	}
	
	private String generateRandomPassword() {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder(8);
		for (int i = 0; i < 8; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}
		return sb.toString();
	}
	
	public UserResponse getMemberByEmail(String email) {
		return memberMapper.findByEmail(email).map(UserResponse::fromEntity).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
	}
}
