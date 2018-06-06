<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
<title>Computer Database</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->
<link href="${pageContext.request.contextPath}/css/bootstrap.min.css"
	rel="stylesheet" media="screen">
<link href="${pageContext.request.contextPath}/css/font-awesome.css"
	rel="stylesheet" media="screen">
<link href="${pageContext.request.contextPath}/css/main.css"
	rel="stylesheet" media="screen">
</head>
<body>
	<header class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<a class="navbar-brand"
				href="${pageContext.request.contextPath}/dashboard"> Application
				- <spring:message code="header.applicationName" />
			</a>
		</div>
	</header>

	<section id="main">
		<div class="container">
			<c:if test="${not empty param.signedout}">
				<div class="alert alert-warning alert-dismissible fade in">
					<spring:message code="login.signed_out" />
	            </div>
			</c:if>
		
			<form action="${pageContext.request.contextPath}/login" method="POST">
				<input type="hidden" name="${_csrf.parameterName}"
					value="${_csrf.token}" />
				<fieldset>
					<legend>
						<spring:message code="login.login" />
					</legend>
					<div class="form-group">
						<label for="username"><spring:message
								code="login.username" /></label> <input type="text"
							class="form-control" id="username" name="username" required>
					</div>
					<div class="form-group">
						<label for="password"><spring:message
								code="login.password" /></label> <input type="password"
							class="form-control" id="password" name="password" required>
					</div>
					<div class="form-actions">
						<button type="submit" class="btn btn-primary">Log in</button>
					</div>
				</fieldset>
			</form>
		</div>
	</section>

	<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>

</body>
</html>