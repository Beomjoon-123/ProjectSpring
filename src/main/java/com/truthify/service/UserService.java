package com.truthify.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.truthify.dao.UserDao;
import com.truthify.domain.User;
import com.truthify.user.dto.UserRequest;
import com.truthify.user.dto.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserDao userDao;

	public UserResponse getMemberByLoginId(String loginId) {
		User user = userDao.getMemberByLoginId(loginId);
		if (user == null) {
			return null;
		}
		return UserResponse.fromEntity(user);
	}

	public UserResponse getMemberById(int id) {
		User user = userDao.getMemberById(id);
		if (user == null) {
			throw new IllegalArgumentException("존재하지 않는 회원입니다");
		}
		return UserResponse.fromEntity(user);
	}

	public List<UserResponse> getAllUsers() {
		return userDao.getAllMembers().stream().map(UserResponse::fromEntity).collect(Collectors.toList());
	}

	@Transactional
	public UserResponse joinMember (UserRequest request) {
		User existingUser = userDao.getMemberByLoginId(request.getLoginId());
		if (existingUser != null) {
			throw new IllegalArgumentException("이미 존재하는 아이디입니다");
		}
		
		User user = User.builder().loginId(request.getLoginId()).loginPw(request.getLoginPw()).email(request.getEmail()).nickname(request.getNickname()).role("USER")
					.build();
		
		userDao.joinMember(user);
		
		return UserResponse.fromEntity(user);
	}
	
	@Transactional
	public UserResponse updateUser(int id, UserRequest request) {
		User user = userDao.getMemberById(id);
		if (user == null) {
			throw new IllegalArgumentException("찾을수 없는 유저입니다");
		}
		
		User userToUpdate = User.builder().id(id).nickname(request.getNickname()).loginPw(request.getLoginPw()).build();
		
		userDao.modifyMember(userToUpdate);
		
		return getMemberById(id);
	}
	
	@Transactional
	public void deleteUser(int id) {
		userDao.deleteMember(id);
	}
}
