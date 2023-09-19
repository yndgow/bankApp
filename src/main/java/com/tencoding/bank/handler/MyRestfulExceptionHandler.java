package com.tencoding.bank.handler;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tencoding.bank.handler.exception.CustomRestfulException;
import com.tencoding.bank.handler.exception.UnAuthorizedException;

/*
 *  예외 발생시 (Json, Xml)
 *  데이터를 가공해서 내려 줄 수 있다.
 */

@RestControllerAdvice // Ioc 대상 + Aop 대상
@Order(1)
public class MyRestfulExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	public void exception(Exception e) {
		System.out.println("==== 예외 발생 확인1 ====");
		System.out.println(e.getMessage());
		System.out.println("--------------------------");
	}
	
	// 사용자 정의 예외 클래스 활용
	@ExceptionHandler(CustomRestfulException.class)
	public String basicException(CustomRestfulException e) {
		// String <--, <--, <-- String은 생성할 때 마다 메모리 계속 차지하므로
		StringBuffer sb = new StringBuffer();
		sb.append("<script>");
		sb.append("alert(' "+e.getMessage() +" ');"); // 문자열 안에 반드시 ; 붙이기
		sb.append("history.back();");
		sb.append("</script>");
		return sb.toString();
	}
	
	// 로그인을 하지 않았을 경우 예외 처리
	@ExceptionHandler(UnAuthorizedException.class)
	public String notLoginException(UnAuthorizedException e) {
		// String <--, <--, <-- String은 생성할 때 마다 메모리 계속 차지하므로
		StringBuffer sb = new StringBuffer();
		sb.append("<script>");
		sb.append("alert(' "+e.getMessage() +" ');"); // 문자열 안에 반드시 ; 붙이기
		sb.append("history.back();");
		sb.append("</script>");
		return sb.toString();
	}
}

