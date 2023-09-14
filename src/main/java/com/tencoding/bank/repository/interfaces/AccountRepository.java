package com.tencoding.bank.repository.interfaces;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.tencoding.bank.repository.model.Account;

// ibatis -> 2.4버전 이후로 Mybatis로 이름이 변경됨
@Mapper // Mapper 반드시 기술을 해줘야 동작한다.
public interface AccountRepository {
	
	// 뱅크 앱. 계좌와 관련된 기능들
	public int insert(Account account);
	public int updateById(Account account);
	public int deleteById(Integer Id);
	public List<Account> findAll();
	public Account findById(Integer id);
	public List<Account> findByUserId(Integer principalId);
	public Account findByNumber(String number); // 계좌 번호로 계좌 존재 여부 확인
}