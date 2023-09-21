<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/layout/header.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!-- header.jsp -->
<!-- html 디자인 -->
<div class="col-sm-8">
	<h2>계좌 상세보기(인증)</h2>
	<h5>어서 오세요 환영합니다.</h5>
	<div class="bg-light p-md-5 h-75">
		<div class="user--box">
			${principal.username} 님의 계좌 <br>
			계좌번호 : ${account.number} <br>
			잔액 : <fmt:formatNumber value="${account.balance}" />원
		</div>
		<br>
		<div>
			<a href="/account/detail/${account.id}">전체</a>&nbsp;
			<a href="/account/detail/${account.id}?type=deposit">입금</a>&nbsp;
			<a href="/account/detail/${account.id}?type=withdraw">출금</a>&nbsp;
		</div>
		<br>
		<table class="table table-sm table-bordered">
			<thead>
				<tr class="text-center">
					<th>날짜</th>
					<th>보낸이</th>
					<th>받은이</th>
					<th>입출금금액</th>
					<th>계좌잔액</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="history" items="${historyList}">
					<tr class="text-center">
						<td>${history.formathCreatedAt()}</td>
						<td>${history.sender}</td>
						<td>${history.receiver}</td>
						<td class="text-right"><fmt:formatNumber value="${history.amount}" />원</td>
						<td class="text-right">${history.formatBalance()}원</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>
</div>
</div>
<!-- footer.jsp -->
<%@ include file="/WEB-INF/view/layout/footer.jsp"%>