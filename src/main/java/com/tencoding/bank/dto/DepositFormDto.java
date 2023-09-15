package com.tencoding.bank.dto;

import lombok.Data;

@Data
public class DepositFormDto {
	private Long amount;
	private String dAccountNumber;
}
