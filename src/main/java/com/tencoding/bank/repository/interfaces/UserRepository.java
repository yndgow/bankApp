package com.tencoding.bank.repository.interfaces;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

import com.tencoding.bank.dto.SignUpFormDto;
import com.tencoding.bank.repository.model.User;

// ibatis -> 2.4버전 이후로 Mybatis로 이름이 변경됨
@Mapper // Mapper 반드시 기술을 해줘야 동작한다.
public interface UserRepository {
	
	// 뱅크 앱. 유저와 관련된 기능들
	// 매개변수 수정
	public int insert(SignUpFormDto signUpFormDto); // 가입
	public int updateById(User user); // 수정
	public int deleteById(Integer id); // 삭제
	public User findById(Integer id); // 유저 찾기
	public List<User> findAll(); // 관리자 - 회원정보 리스트를 보고 싶다면
}