package com.tencoding.bank.repository.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Getter
//@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

	private Integer id;
	private String username;
	private String password;
	private String fullname;
	private Timestamp createdAt;
	
	
}
