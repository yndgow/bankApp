package com.tencoding.bank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class AccountController {

	// 계좌 목록 페이지
	// http://localhost:80/account/list
	@GetMapping("/list")
	public String list() {
		return "account/list";
	}
	
	// 계좌 생성 페이지
	// http://localhost:80/account/save
	@GetMapping("/save")
	public String save() {
		return "account/save";
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
