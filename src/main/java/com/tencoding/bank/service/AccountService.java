package com.tencoding.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tencoding.bank.dto.DepositFormDto;
import com.tencoding.bank.dto.SaveFormDto;
import com.tencoding.bank.dto.TransferFormDto;
import com.tencoding.bank.dto.WithdrawFormDto;
import com.tencoding.bank.handler.exception.CustomRestfulException;
import com.tencoding.bank.repository.interfaces.AccountRepository;
import com.tencoding.bank.repository.interfaces.HistoryRepository;
import com.tencoding.bank.repository.model.Account;
import com.tencoding.bank.repository.model.History;

@Service // Ioc 대상 + 싱글톤 패턴 -> 스프링 컨테이너 메모리에 객체가 생성
public class AccountService {

	@Autowired // DI - 가지고 오다.
	private AccountRepository accountRepository;

	@Autowired // DI
	private HistoryRepository historyRepository;

	// 계좌 생성하기
	@Transactional
	public void createAccount(SaveFormDto saveFormDto, Integer principalId) {

		// 계좌번호 중복 검사
		Account resultAcc = accountRepository.findByNumber(saveFormDto.getNumber());
		if (resultAcc != null) {
			throw new CustomRestfulException("중복된 계좌번호가 있습니다. 다른 계좌번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
		}

		// 등록 처리 - insert
		Account account = new Account();
		account.setNumber(saveFormDto.getNumber());
		account.setPassword(saveFormDto.getPassword());
		account.setBalance(saveFormDto.getBalance());
		account.setUserId(principalId);

		int resultRowCount = accountRepository.insert(account);

//		System.out.println("resultRowCount : " + resultRowCount);
		if (resultRowCount != 1) {
			throw new CustomRestfulException("계좌 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional
	public List<Account> readAccountList(Integer principalId) {
		List<Account> list = accountRepository.findByUserId(principalId);
		return list;
	}

	// 출금 기능 로직을 고민해보기
	// 1. 계좌 존재 여부 확인 -- select query
	// 2. 본인 계좌 여부 확인
	// 3. 계좌 비밀번호 확인
	// 4. 잔액 여부 확인
	// 5. 출금 처리 -- update query
	// 6. 거래 내역 등록 --> insert query
	// 7. 트랜잭션 처리
	@Transactional
	public void updateAccountWithdraw(WithdrawFormDto withdrawFormDto, Integer userId) {
		Account accountEntity = accountRepository.findByNumber(withdrawFormDto.getWAccountNumber());
		// 1
		if (accountEntity == null) {
			throw new CustomRestfulException("해당 계좌가 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 2
		if (accountEntity.getUserId() != userId) {
			throw new CustomRestfulException("본인 소유 계좌가 아닙니다.", HttpStatus.BAD_REQUEST);
		}

		// 3
		if (!accountEntity.getPassword().equals(withdrawFormDto.getWAccountPassword())) {
			throw new CustomRestfulException("출금계좌 비밀번호가 틀렸습니다.", HttpStatus.BAD_REQUEST);
		}
		// 4
		if (accountEntity.getBalance() < withdrawFormDto.getAmount()) {
			throw new CustomRestfulException("계좌 잔액이 부족합니다.", HttpStatus.BAD_REQUEST);
		}
		// 5 -> update 쿼리 (모델 객체 상태 변경 --> 객체를 다시 던지기)
		accountEntity.withdraw(withdrawFormDto.getAmount()); // model의 메서드 활용
		accountRepository.updateById(accountEntity); // 쿼리 던지기
		// 6 - 거래내역 등록 History 객체 생성
		History history = new History();
		history.setAmount(withdrawFormDto.getAmount()); // 출금금액
		history.setWBalance(accountEntity.getBalance()); // 잔액
		history.setWAccountId(accountEntity.getId());
		history.setDAccountId(null);

		int resultRowCount = historyRepository.insert(history);
		if (resultRowCount != 1) {
			throw new CustomRestfulException("정상 처리되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// 입금 기능 로직 생각 해보기
	// 1. 계좌 존재 여부 확인 -- select query
	// 2. 입금 처리 --> update query
	// 3. 거래내역 등록 --> insert query
	@Transactional
	public void updateAccountDeposit(DepositFormDto depositFormDto) {
		
		Account accountEntity = accountRepository.findByNumber(depositFormDto.getDAccountNumber());
		
		// 1
		if(accountEntity == null) {
			throw new CustomRestfulException("입력한 계좌가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		}
		
		// 2
		accountEntity.deposit(depositFormDto.getAmount()); // entity 입금 업데이트
		accountRepository.updateById(accountEntity); // 입금 쿼리
		
		// 3
		History history = new History();
		history.setAmount(depositFormDto.getAmount());
		history.setDAccountId(accountEntity.getId());
		history.setDBalance(accountEntity.getBalance());
		int resultRowCount = historyRepository.insert(history);
		if(resultRowCount != 1) {
			throw new CustomRestfulException("정상적으로 입금되지 않았습니다. ", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 이체 로직
	// 1. 출금 계좌 확인 - select
	// 2. 입금 계좌 확인 - select
	// 3. 출금 계좌 본인 소유 확인 - 객체 상태값 확인(1 = userId)
	// 4. 출금 계좌 비밀번호 확인 - DTO = 1
	// 5. 출금 계좌 잔액 확인 - DTO = 1
	// 6. 출금 계좌 잔액 빼기 - update
	// 7. 입금 계좌 잔액 더하기 - update
	// 8. 거래 내역 등록
	// 9. 트랜잭션
	@Transactional
	public void updateAccountTransfer(TransferFormDto transferFormDto, Integer userId) {
		// 1
		Account withdrawAccountEntity = accountRepository.findByNumber(transferFormDto.getWAccountNumber());
		if(withdrawAccountEntity == null) throw new CustomRestfulException("출금계좌가 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		// 2
		Account depositAccountEntity = accountRepository.findByNumber(transferFormDto.getDAccountNumber());
		if(depositAccountEntity == null) throw new CustomRestfulException("입금계좌가 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		// 3
		withdrawAccountEntity.checkOwner(userId);
		// 4
		withdrawAccountEntity.checkPassword(transferFormDto.getWAccountPassword());
		// 5
		withdrawAccountEntity.checkBalance(transferFormDto.getAmount());
		// 6 출금잔액 상태값 변경
		withdrawAccountEntity.withdraw(transferFormDto.getAmount());
		// 6 update 처리
		accountRepository.updateById(withdrawAccountEntity);
		// 7 입금잔액 상태값 변경
		depositAccountEntity.deposit(transferFormDto.getAmount());
		// 7 update 처리
		accountRepository.updateById(depositAccountEntity);
		// 8
		History history = new History();
		history.setAmount(transferFormDto.getAmount());
		history.setWAccountId(withdrawAccountEntity.getId());
		history.setDAccountId(depositAccountEntity.getId());
		history.setWBalance(withdrawAccountEntity.getBalance());
		history.setDBalance(depositAccountEntity.getBalance());
		int resultRowCount = historyRepository.insert(history);
		if(resultRowCount != 1) {
			throw new CustomRestfulException("정상 처리되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
