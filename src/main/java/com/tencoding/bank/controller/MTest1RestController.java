package com.tencoding.bank.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/macc")
public class MTest1RestController {

	// http://localhost:80/macc/test
	@GetMapping("/test")
	public String test1() {
//		new CustomRestfulException("잘못된 연산", HttpStatus.BAD_REQUEST);
		return "정상 동작";
	}
}
