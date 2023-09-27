package com.tencoding.bank.controller;


import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencoding.bank.dto.KakaoProfile;
import com.tencoding.bank.dto.OAuthToken;
import com.tencoding.bank.dto.SignInFormDto;
import com.tencoding.bank.dto.SignUpFormDto;
import com.tencoding.bank.handler.exception.CustomRestfulException;
import com.tencoding.bank.repository.model.User;
import com.tencoding.bank.service.UserService;
import com.tencoding.bank.utils.Define;

@Controller
@RequestMapping({"/user", "/"})
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private HttpSession session;
	@Value("${tenco.key}")
	private String tencoKey;
	
	// 회원 가입 페이지 요청
	// http://localhost:80/user/sign-up
	@GetMapping("/sign-up")
	public String signUp() {
		// /WEB-INF/view
		// /WEB-INF/view/user/--return String--
		// .jsp
		return "user/signUp";
	}

	// 로그인 페이지 요청
	// http://localhost:80/user/sign-in
	@GetMapping({"/sign-in", ""})
	public String signIn() {
		return "user/signIn";
	}

	// 회원 가입 처리
	// http://localhost:80/user/sign-up
	// POST - HTTP body (데이터)
	// name 속성을 이용해서
	// key = value 구조로 파싱
	// ObjectMapper 가 내부적으로 작동
	/**
	 * 회원가입
	 * @param signUpFormDto
	 * @return 리다이렉트 처리 - 로그인 페이지
	 */
	@PostMapping("/sign-up")
	public String signUpProc(SignUpFormDto signUpFormDto) {
		// 1. 유효성 검사
		if (signUpFormDto.getUsername() == null || signUpFormDto.getUsername().isEmpty()) {
			throw new CustomRestfulException("username을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		if (signUpFormDto.getPassword() == null || signUpFormDto.getPassword().isEmpty()) {
			throw new CustomRestfulException("password을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		if (signUpFormDto.getFullname() == null || signUpFormDto.getFullname().isEmpty()) {
			throw new CustomRestfulException("fullname을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		// 2. 서비스 호출
		userService.signUp(signUpFormDto);
		// 3. 정상 처리 되었다면
		return "redirect:/user/sign-in";

		// 사용자 이미지..
	}
	
	/**
	 * 로그인
	 * @param signInFormDto
	 * @return 계좌 리스트 페이지로 리턴
	 */
	@PostMapping("/sign-in")
	public String signInProc(SignInFormDto signInFormDto) {
		// 1. 유효성 검사
		if(signInFormDto.getUsername() == null || signInFormDto.getUsername().isEmpty()) {
			throw new CustomRestfulException("username을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if(signInFormDto.getPassword() == null || signInFormDto.getPassword().isEmpty()) {
			throw new CustomRestfulException("password를 입력하시오", HttpStatus.BAD_REQUEST);
		}
		
		// 2. 서비스 -> 인증된 사용자 여부 확인
		User principal = userService.signIn(signInFormDto);
		principal.setPassword(null);
				
		// 3. 쿠키 + 세션
		session.setAttribute(Define.PRINCIPAL, principal);
		
		return "redirect:/account/list";
	}
	
	/**
	 * 로그아웃 처리
	 * @return 리다이렉트 - 로그인 페이지 이동
	 */
	@GetMapping("/logout")
	public String logout() {
		session.invalidate();
		return "redirect:/user/sign-in";
	}
	
	
	// http://localhost:80/user/kakao/callback?code="authCode"
	@GetMapping("/kakao/callback")
	//@ResponseBody // data 반환 명시
	public String kakaoCallback(String code) {
//		System.out.println("메서드 동작");
		
		// POST 요청 - exchange() 메서드 활용
		RestTemplate rt = new RestTemplate();
		
		// Header 구성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// body 구성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "d756e532cfa99b1914e7dffa847d96ab");
		params.add("redirect_uri", "http://localhost:80/user/kakao/callback");
		params.add("code", code);
		
		// HttpEntity 결합 (헤더 + 바디)
		HttpEntity<MultiValueMap<String, String>> reqMes = new HttpEntity<>(params, headers);
		
		// HTTP 요청
		ResponseEntity<OAuthToken> response = rt.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST, reqMes, OAuthToken.class);
		
		
		// 0927
		// 1. DTO 파싱
//		System.out.println("액세스 토큰 확인 " + response.getBody().toString());
		// 액세스 토큰
		// 액세스 토큰 --> 카카오 서버(정보)
		
		// 문서 확인 - 정보 요청 주소 형식 https://kapi.kakao.com/v2/user/me GET or POST
//		System.out.println("-------------------------");
		RestTemplate ret2 = new RestTemplate();
		
		// 헤더 생성
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer " + response.getBody().getAccessToken());
		headers2.add("Content-type", "Content-type: application/x-www-form-urlencoded;charset=utf-8");
		
		// 바디 생성 - 생략
		// 결합
		HttpEntity<MultiValueMap<String, String>> kakaoInfo = new HttpEntity<>(headers2);
		ResponseEntity<KakaoProfile> response2 = ret2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, kakaoInfo, KakaoProfile.class);
//		System.out.println("--------------res2--------------------");
//		System.out.println(response2.getBody().getKakaoAccount().getEmail());
//		System.out.println("-------------카카오 서버에 정보받기 완료-------------");
		
		// 1. 회원가입 여부 확인
		// - 최초 사용자라면 본인 회원가입 형식으로 회원가입 처리
		// DB -> user_tb --> username, password, fullname
		// password <-- 직접 만들어서 넣어야 합니다.
		// 소셜 로그인 사용자는 모든 패스워드 동일합니다.
		// username -> 동일한 값이 저장되지 않도록 처리
		
		KakaoProfile kakaoProfile = response2.getBody();
		
		SignUpFormDto signUpFormDto = SignUpFormDto
									.builder()
									.username(kakaoProfile.getKakaoAccount().getEmail() + "_" + kakaoProfile.getId())
									.fullname("OAuth_" + kakaoProfile.getKakaoAccount().getEmail())
									.password(tencoKey)
									.build();
		
		// User, null
		User oldUser = userService.searchUsername(signUpFormDto.getUsername());
		if(oldUser == null) {
			// 사용자가 최초 소셜 로그인 사용자면 자동 회원 가입처리
			userService.signUp(signUpFormDto);
			oldUser = User
					.builder()
					.username(signUpFormDto.getUsername())
					.fullname(signUpFormDto.getFullname())
					.build(); 
		}
		
		
		// 그게 아니라면 바로 세션에 데이터 등록 로그인 처리
		session.setAttribute(Define.PRINCIPAL, oldUser);
		
		return "redirect:/account/list";
	}
	
	
	// 카카오 로그아웃
	// https://kapi.kakao.com/v1/user/logout
	@GetMapping("/kakao/logout")
	public String kakaoLogout() {
		RestTemplate rt3 = new RestTemplate();
		String url = "https://kapi.kakao.com/v1/user/logout";
//		HttpMethod httpMethod = HttpMethod.POST;
		
		// 헤더
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "KakaoAK 393277da15284a18128af283329bcdd1");
		
		// 바디
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		// 고정값
		body.add("target_id_type", "user_id");
		// 유저번호 획득
		User user = (User)session.getAttribute(Define.PRINCIPAL);
		String username = user.getUsername();
		int _index = username.lastIndexOf("_");
		String kakaoUserNum = username.substring(_index + 1);
		body.add("target_id", kakaoUserNum);
		
		// 바디 + 헤더 결합
		HttpEntity<MultiValueMap<String, String>> kakaoLogoutInfo = new HttpEntity<>(body, httpHeaders);
		
		// exchange
		ResponseEntity<String> resKakaoLogout = rt3.exchange(url, HttpMethod.POST, kakaoLogoutInfo, String.class);
		
//		System.out.println(resKakaoLogout);
		
		// 잭슨 오브젝트 매퍼를 활용하여 class로 변환
		ObjectMapper objectMapper = new ObjectMapper();
		KakaoProfile kakaoProfile = null;
		try {
			kakaoProfile = objectMapper.readValue(resKakaoLogout.getBody(), KakaoProfile.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		session.invalidate();
		
		System.out.println("로그아웃한 회원은 카카오회원번호 : " + kakaoProfile.getId());
		
		return "redirect:/user/sign-in";
	}

}

