<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/layout/header.jsp"%>
<!-- header.jsp -->
<!-- html 디자인 -->
		<div class="col-sm-8">
			<h2>로그인 페이지</h2>
			<h5>어서 오세요 환영합니다.</h5>
			<div class="bg-light p-md-5 h-75">
			<!--  로그인은 보안때문에 예외적으로 post 방식을 활용한다.-->
				<form action="/user/sign-in" method="post">
					<div class="form-group">
						<label for="username">username :</label>
						<input type="text" id="username" class="form-control" placeholder="Enter username" name="username" value="길동">
					</div>
					<div class="form-group">
						<label for="pwd">password :</label>
						<input type="password" id="pwd" class="form-control" placeholder="Enter password" name="password" value="1234">
					</div>
					<button type="submit" class="btn btn-primary">Submit</button>
					<a href="https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=d756e532cfa99b1914e7dffa847d96ab&redirect_uri=http://localhost:80/user/kakao/callback"><img alt="kakaoLogin" src="/images/kakao_login_medium.png" width="74" height="38"></a>
				</form>
			</div>
		</div>
	</div>
</div>
<!-- footer.jsp -->
<%@ include file="/WEB-INF/view/layout/footer.jsp"%>