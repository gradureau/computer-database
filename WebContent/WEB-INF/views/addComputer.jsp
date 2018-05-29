<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
<title>Computer Database</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->
<link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="${pageContext.request.contextPath}/css/font-awesome.css" rel="stylesheet" media="screen">
<link href="${pageContext.request.contextPath}/css/main.css" rel="stylesheet" media="screen">
</head>
<body>
    <header class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard"> Application - <spring:message code="header.applicationName"/> </a>
            <a class="navbar-brand pull-right" href="?lang=<spring:message code="header.alter_language"/>">
			<img src="${pageContext.request.contextPath}/images/flag_<spring:message code="header.alter_language"/>.png"
			style="height:50px;width:50px;margin:-15px;" alt="change language icon" />
			</a>
        </div>
    </header>

    <section id="main">
        <div class="container">
            <div class="row">
                <div class="col-xs-8 col-xs-offset-2 box">
                    <h1><spring:message code="add_computer.add_computer"/></h1>
                    <form:form action="add-computer" method="POST" modelAttribute="computerData">
                        <fieldset>
                            <div class="form-group">
                                <label for="computerName"><spring:message code="computer.computer_name"/></label>
                                <input type="text" class="form-control" id="computerName" name="computerName" placeholder="Computer name" required>
                            </div>
                            <div class="form-group">
                                <label for="introduced"><spring:message code="computer.introduced_date"/></label>
                                <input type="date" class="form-control" id="introduced" name="introduced" placeholder="Introduced date">
                            </div>
                            <div class="form-group">
                                <label for="discontinued"><spring:message code="computer.discontinued_date"/></label>
                                <input type="date" class="form-control" id="discontinued" name="discontinued" placeholder="Discontinued date">
                            </div>
                            <div class="form-group">
                                <label for="companyId"><spring:message code="computer.company"/></label>
                                <select class="form-control" id="companyId" name="companyId">
                                    <option value="0">--</option>
                                    <c:forEach var="company" items="${requestScope.companies}">
                                    <option value="${company.id}">${company.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </fieldset>
                        <div class="actions pull-right">
                            <input type="submit" value="<spring:message code="add_computer.add"/>" class="btn btn-primary">
                            or
                            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-default"><spring:message code="add_computer.cancel"/></a>
                        </div>
                    </form:form>
                </div>
                <c:if test="${not empty warning}">
    			<div class="col-xs-8 col-xs-offset-2 box">
		            <div class="alert alert-warning alert-dismissible fade in">
		                ${warning}
		            </div>
		        </div>
				</c:if>
            </div>
        </div>
    </section>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datePrecedenceValidator.js"></script>
</body>
</html>