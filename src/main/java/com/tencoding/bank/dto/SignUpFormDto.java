package com.tencoding.bank.dto;

import com.tencoding.bank.repository.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpFormDto {
	private String username;
	private String password;
	private String fullname;
	
	
	// TODO - 추후 추가 예정
	
	public User fromEntity() {
		return User.builder().username(username).password(password).fullname(fullname).build();
	}
	
	public SignUpFormDto toEntity(User user) {
		return SignUpFormDto.builder().username(user.getUsername()).build();
		
	}
}
