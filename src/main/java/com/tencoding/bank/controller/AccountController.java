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

import com.tencoding.bank.dto.SaveFormDto;
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

	// 출금 페이지
	// http://localhost:80/account/withdraw
	@GetMapping("/withdraw")
	public String withdraw() {
		return "account/withdraw";
	}

	// 입금 페이지
	// http://localhost:80/account/deposit
	@GetMapping("/deposit")
	public String deposit() {
		return "account/deposit";
	}

	// 이체 페이지
	// http://localhost:80/account/transfer
	@GetMapping("/transfer")
	public String transfer() {
		return "account/transfer";
	}

	// TODO - 수정하기
	// 상세 보기 페이지
	// http://localhost:80/account/detail/ACCOUNT_ID
	@GetMapping("/detail")
	public String detail() {
		return "account/detail";
	}

}
