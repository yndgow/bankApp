package com.tencoding.bank.dto;

import lombok.Data;

@Data
public class TransferFormDto {
	private Long amount;
	private String wAccountNumber;
	private String dAccountNumber;
	private String wAccountPassword;
}
