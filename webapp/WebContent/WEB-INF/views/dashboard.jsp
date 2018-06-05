<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>
<%@ taglib uri="cdb-tags" prefix="mylib" %>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
<title>Computer Database</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta charset="utf-8">
<!-- Bootstrap -->
<link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"
	media="screen">
<link href="${pageContext.request.contextPath}/css/font-awesome.css" rel="stylesheet" media="screen">
<link href="${pageContext.request.contextPath}/css/main.css" rel="stylesheet" media="screen">
</head>
<body>
	<header class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard"> Application -
				<spring:message code="header.applicationName"/> </a>
			<a class="navbar-brand pull-right" href="?lang=<spring:message code="header.alter_language"/>">
			<img src="${pageContext.request.contextPath}/images/flag_<spring:message code="header.alter_language"/>.png"
			style="height:50px;width:50px;margin:-15px;" alt="change language icon" />
			</a>
		</div>
	</header>

	<section id="main">
		<div class="container">
			<h1 id="homeTitle">${requestScope.resultsFound} <spring:message code="dashboard.computers_found"/></h1>
			<div id="actions" class="form-horizontal">
				<div class="pull-left">
					<form id="searchForm" action="dashboard" method="GET" class="form-inline">

						<input type="search" id="searchbox" name="search"
							class="form-control" placeholder="<spring:message code="dashboard.search_name"/>" value="${requestScope.searchedKeyWords}" /> <input
							type="submit" id="searchsubmit" value="<spring:message code="dashboard.filter_name"/>"
							class="btn btn-primary" />
					</form>
				</div>
				<div class="pull-right">
					<a class="btn btn-success" id="addComputer" href="${pageContext.request.contextPath}/add-computer">
					<spring:message code="dashboard.add_computer"/></a> <a class="btn btn-default" id="editComputer" href="#"
						onclick="$.fn.toggleEditMode();"><spring:message code="dashboard.edit"/></a>
				</div>
			</div>
		</div>

		<form id="deleteForm" action="delete-computers" method="POST">
			<input type="hidden" name="selection" value="">
		</form>

		<div class="container" style="margin-top: 10px;">
			<table class="table table-striped table-bordered">
				<thead>
					<tr>
						<!-- Variable declarations for passing labels as parameters -->
						<!-- Table header for Computer Name -->

						<th class="editMode" style="width: 60px; height: 22px;"><input
							type="checkbox" id="selectall" /> <span
							style="vertical-align: top;"> - <a href="${pageContext.request.contextPath}/#"
								id="deleteSelected" onclick="$.fn.deleteSelected();"> <i
									class="fa fa-trash-o fa-lg"></i>
							</a>
						</span></th>
						<th><spring:message code="computer.computer_name"/></th>
						<th><spring:message code="computer.introduced_date"/></th>
						<th><spring:message code="computer.discontinued_date"/></th>
						<th><spring:message code="computer.company"/></th>

					</tr>
				</thead>
				<!-- Browse attribute computers -->
				<tbody id="results">
					<c:forEach var="computer" items="${computers}">
						<tr>
							<td class="editMode"><input type="checkbox"
								name="deleteItem" class="cb" value="${computer.id}"></td>
							<td><a href="${pageContext.request.contextPath}/edit-computer/${computer.id}" onclick="">${computer.name}</a>
							</td>
							<td><javatime:format value="${computer.introduced}"
									style="M-" /></td>
							<td><javatime:format value="${computer.discontinued}"
									style="M-" /></td>
							<td>${computer.companyName}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</section>

	<footer class="navbar-fixed-bottom">
		<div class="container text-center">
			<mylib:pagination uri="${requestScope.uri}" page="${requestScope.page}" refreshCount="FALSE"/>
		</div>

	</footer>
	<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>

</body>
</html>