package com.tencoding.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tencoding.bank.dto.SignInFormDto;
import com.tencoding.bank.dto.SignUpFormDto;
import com.tencoding.bank.handler.exception.CustomRestfulException;
import com.tencoding.bank.repository.interfaces.UserRepository;
import com.tencoding.bank.repository.model.User;

@Service // Ioc 대상 - 싱글톤 패턴
public class UserService {

	// DAO - 데이터베이스
	@Autowired
	private UserRepository userRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	
	// DI - 가지고 오다.
//	public UserService(UserRepository userRepository) {
//		this.userRepository = userRepository;
//	}

	/**
	 * 회원가입
	 * 비밀번호 암호화 처리
	 * @param signUpFormDto
	 */
	// 트랜잭션 사용하는 이유는 정상처리되면 commit(반영)
	// 정상처리가 안되면 Rollback 처리 된다.
	@Transactional
	public void signUp(SignUpFormDto signUpFormDto) {
		
		// 비밀번호 암호화
		String rawPwd = signUpFormDto.getPassword();
		System.out.println(rawPwd);
		String hashPwd = passwordEncoder.encode(rawPwd);
		System.out.println(hashPwd);
		signUpFormDto.setPassword(hashPwd);
		
		
		int result = userRepository.insert(signUpFormDto);
		if (result != 1) {
			throw new CustomRestfulException("회원가입실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * username, password 일치 확인
	 * @param signInFormDto
	 * @return userEntity
	 */
	// 로그인 서비스 처리
	public User signIn(SignInFormDto signInFormDto) {
		// 계정확인
		User userEntity = userRepository.findByUsername(signInFormDto.getUsername());
		// 계정이 존재하지 않습니다.
		if(userEntity == null || !userEntity.getUsername().equals(signInFormDto.getUsername())) {
			throw new CustomRestfulException("계정이 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// 비밀번호 확인
		boolean isPwdMatched = passwordEncoder.matches(signInFormDto.getPassword(), userEntity.getPassword());
		// 비밀번호가 틀렸습니다.
		if(!isPwdMatched) {
			throw new CustomRestfulException("비밀번호가 틀렸습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userEntity;
	}
	
	/**
	 * 유저 찾기
	 * @param username
	 * @return
	 */
	public User searchUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public void testForm(User user) {
		SignUpFormDto signUpFormDto = new SignUpFormDto().toEntity(user);
		
		
		userRepository.insert(signUpFormDto);
	}
	
	
}
