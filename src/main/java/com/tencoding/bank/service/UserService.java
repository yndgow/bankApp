package com.tencoding.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

	// DI - 가지고 오다.
//	public UserService(UserRepository userRepository) {
//		this.userRepository = userRepository;
//	}

	// 트랜잭션 사용하는 이유는 정상처리되면 commit(반영)
	// 정상처리가 안되면 Rollback 처리 된다.
	@Transactional
	public void signUp(SignUpFormDto signUpFormDto) {
		int result = userRepository.insert(signUpFormDto);
		System.out.println("result : " + result);
		if (result != 1) {
			throw new CustomRestfulException("회원가입실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 로그인 서비스 처리
	public User signIn(SignInFormDto signInFormDto) {
		User userEntity = userRepository.findByusernameAndPassword(signInFormDto);
		if(userEntity == null) {
			throw new CustomRestfulException("아이디 혹은 비번이 틀렸습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userEntity;
	}
}
