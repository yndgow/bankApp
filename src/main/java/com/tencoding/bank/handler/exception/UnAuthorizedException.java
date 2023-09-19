package com.tencoding.bank.handler.exception;

import org.springframework.http.HttpStatus;

public class UnAuthorizedException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private HttpStatus status;

	public UnAuthorizedException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
}