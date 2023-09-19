package com.tencoding.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tencoding.bank.dto.SaveFormDto;
import com.tencoding.bank.handler.exception.CustomRestfulException;
import com.tencoding.bank.repository.interfaces.AccountRepository;
import com.tencoding.bank.repository.model.Account;

@Service // Ioc 대상 + 싱글톤 패턴 -> 스프링 컨테이너 메모리에 객체가 생성
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;

	// 계좌 생성하기
	@Transactional
	public void createAccount(SaveFormDto saveFormDto, Integer principalId) {
		
		// 계좌번호 중복 검사
		Account resultAcc = accountRepository.findByNumber(saveFormDto.getNumber());
		if(resultAcc != null) {
			throw new CustomRestfulException("중복된 계좌번호가 있습니다. 다른 계좌번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
		}
		
		// 등록 처리 - insert
		Account account = new Account();
		account.setNumber(saveFormDto.getNumber());
		account.setPassword(saveFormDto.getPassword());
		account.setBalance(saveFormDto.getBalance());
		account.setUserId(principalId);
		
		int	resultRowCount = accountRepository.insert(account);
		
//		System.out.println("resultRowCount : " + resultRowCount);
		if(resultRowCount != 1) {
			throw new CustomRestfulException("계좌 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Transactional
	public List<Account> readAccountList(Integer principalId) {
		List<Account> list = accountRepository.findByUserId(principalId);
		return list;
	}

}
