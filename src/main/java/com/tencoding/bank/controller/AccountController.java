package com.tencoding.bank.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tencoding.bank.dto.DepositFormDto;
import com.tencoding.bank.dto.SaveFormDto;
import com.tencoding.bank.dto.TransferFormDto;
import com.tencoding.bank.dto.WithdrawFormDto;
import com.tencoding.bank.handler.exception.CustomRestfulException;
import com.tencoding.bank.handler.exception.UnAuthorizedException;
import com.tencoding.bank.repository.model.Account;
import com.tencoding.bank.repository.model.User;
import com.tencoding.bank.service.AccountService;
import com.tencoding.bank.utils.Define;

@Controller
@RequestMapping("/account")
public class AccountController {

	@Autowired // DI 처리
	private HttpSession session;
	@Autowired
	private AccountService accountService;

	// 계좌 목록 페이지
	// http://localhost:80/account/list
	@GetMapping("/list")
	public String list(Model model) {
		// 1. 인증 여부 확인
		User user = (User) session.getAttribute(Define.PRINCIPAL);
		if (user == null) {
			throw new UnAuthorizedException("로그인 먼저 해요", HttpStatus.UNAUTHORIZED);
		}

		// 2. 서비스 로직
		List<Account> accounts = accountService.readAccountList(user.getId());
		model.addAttribute("accounts", accounts);
		return "account/list";
	}

	/**
	 * 계좌생성페이지 이동
	 */
	// http://localhost:80/account/save
	@GetMapping("/save")
	public String save() {
		// 1. 인증 여부 확인
		User user = (User) session.getAttribute(Define.PRINCIPAL);
		if (user == null) {
			throw new UnAuthorizedException("로그인 먼저 해요", HttpStatus.UNAUTHORIZED);
		}

		return "account/save";
	}

	/**
	 * 계좌 생성 로직 구현
	 * 
	 * @return account/save
	 */
	@PostMapping("/save")
	public String saveProc(SaveFormDto saveFormDto) {
		// 1. 인증
		User user = (User) session.getAttribute(Define.PRINCIPAL);
		if (user == null) {
			throw new UnAuthorizedException("로그인 먼저 해요", HttpStatus.UNAUTHORIZED);
		}

		// 2. 유효성 검사
		if (saveFormDto.getNumber() == null || saveFormDto.getNumber().isEmpty())
			throw new CustomRestfulException("계좌번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		if (saveFormDto.getPassword() == null || saveFormDto.getPassword().isEmpty())
			throw new CustomRestfulException("계좌비밀번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		if (saveFormDto.getBalance() == null || saveFormDto.getBalance() < 0)
			throw new CustomRestfulException("잘못된 입력입니다.", HttpStatus.BAD_REQUEST);

		// 3. 서비스 로직
		accountService.createAccount(saveFormDto, user.getId());
		return "redirect:/account/save";
	}

	/**
	 * 출금 페이지 이동
	 * 
	 * @return /account/withdraw.jsp
	 */
	// 출금 페이지
	// http://localhost:80/account/withdraw
	@GetMapping("/withdraw")
	public String withdraw() {
		// 1. 인증
		User user = (User) session.getAttribute(Define.PRINCIPAL);
		if (user == null) {
			throw new UnAuthorizedException("로그인 먼저 해요", HttpStatus.UNAUTHORIZED);
		}
		return "account/withdraw";
	}

	// body -> String --> amout=1000&wAcoountId=10&...
	@PostMapping("/withdraw")
	public String withdrawProc(WithdrawFormDto withdrawFormDto) {
		// 1. 인증
		User user = (User) session.getAttribute(Define.PRINCIPAL);
		if (user == null) {
			throw new UnAuthorizedException("로그인 먼저 해요", HttpStatus.UNAUTHORIZED);
		}

		// 2. 유효성 검사
		if (withdrawFormDto.getAmount() == null) {
			throw new CustomRestfulException("금액을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if (withdrawFormDto.getAmount() <= 0) {
			throw new CustomRestfulException("0원 이하일 수 없습니다.", HttpStatus.BAD_REQUEST);
		}
		if (withdrawFormDto.getWAccountNumber() == null) {
			throw new CustomRestfulException("출금계좌번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if (withdrawFormDto.getWAccountPassword() == null) {
			throw new CustomRestfulException("출금계좌 비밀번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}

		// 3. 서비스 로직
		accountService.updateAccountWithdraw(withdrawFormDto, user.getId());
		return "redirect:/account/list";
	}

	// 입금 페이지
	// http://localhost:80/account/deposit
	@GetMapping("/deposit")
	public String deposit() {
		// 1. 인증 여부 확인
		User user = (User) session.getAttribute(Define.PRINCIPAL);
		if (user == null) {
			throw new UnAuthorizedException("로그인 먼저 해요", HttpStatus.UNAUTHORIZED);
		}
		return "account/deposit";
	}

	@PostMapping("/deposit")
	public String depositProc(DepositFormDto depositFormDto) {
		// 1. 인증 여부 확인
		User user = (User) session.getAttribute(Define.PRINCIPAL);
		if (user == null) {
			throw new UnAuthorizedException("로그인 먼저 해요", HttpStatus.UNAUTHORIZED);
		}
		// 2. 유효성 검사
		if (depositFormDto.getAmount() <= 0) {
			throw new CustomRestfulException("금액이 0원 이하입니다.", HttpStatus.BAD_REQUEST);
		}
		if (depositFormDto.getAmount() == null) {
			throw new CustomRestfulException("금액을 입력해주세요.", HttpStatus.BAD_REQUEST);
		}
		if (depositFormDto.getDAccountNumber() == null || depositFormDto.getDAccountNumber().isEmpty()) {
			throw new CustomRestfulException("계좌번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}

		// 3. 서비스 로직
		accountService.updateAccountDeposit(depositFormDto);

		return "redirect:/account/list";
	}

	/**
	 * 계좌이체 이동 페이지
	 * 
	 * @return account/transfer
	 */
	// 이체 페이지
	// http://localhost:80/account/transfer
	@GetMapping("/transfer")
	public String transfer() {
		// 1. 인증 여부 확인
		User user = (User) session.getAttribute(Define.PRINCIPAL);
		if (user == null) {
			throw new UnAuthorizedException("로그인 먼저 해요", HttpStatus.UNAUTHORIZED);
		}
		return "account/transfer";
	}

	/**
	 * 1. 출금계좌번호 입력 여부 2. 입금계좌번호 입력 여부 3. 출금계좌 비밀번호 입력 여부 4. 이체금액 0원이상
	 * 
	 * @param transferFormDto
	 * @return account/list
	 */
	@PostMapping("/transfer")
	public String transferProc(TransferFormDto transferFormDto) {
		// 1. 인증 검사
		User user = (User) session.getAttribute(Define.PRINCIPAL);
		if (user == null) {
			throw new UnAuthorizedException("로그인 먼저 해요", HttpStatus.UNAUTHORIZED);
		}
		// 2. 유효성 검사
		if (transferFormDto.getWAccountNumber() == null || transferFormDto.getWAccountNumber().isEmpty()) {
			throw new CustomRestfulException("출금계좌번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		if (transferFormDto.getDAccountNumber() == null || transferFormDto.getDAccountNumber().isEmpty()) {
			throw new CustomRestfulException("입금계좌번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		if (transferFormDto.getWAccountPassword() == null || transferFormDto.getWAccountPassword().isEmpty()) {
			throw new CustomRestfulException("출금계좌 비밀번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		if (transferFormDto.getAmount() <= 0) {
			throw new CustomRestfulException("이체 금액이 0원이하 입니다.", HttpStatus.BAD_REQUEST);
		}

		// 3. 서비스 로직
		accountService.updateAccountTransfer(transferFormDto, user.getId());
		return "redirect:/account/list";
	}

	// TODO - 수정하기
	// 상세 보기 페이지
	// http://localhost:80/account/detail/ACCOUNT_ID
	@GetMapping("/detail")
	public String detail() {
		return "account/detail";
	}

}
