<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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
            <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard"> Application - Computer Database </a>
        </div>
    </header>
    <section id="main">
        <div class="container">
            <div class="row">
                <div class="col-xs-8 col-xs-offset-2 box">
                    <div class="label label-default pull-right">
                        id: ${computer.id}
                    </div>
                    <h1>Edit Computer</h1>

                    <form:form action="${pageContext.request.contextPath}/edit-computer/" method="POST" modelAttribute="computerData">
                        <input type="hidden" value="${computer.id}" id="id" name="id" />
                        <fieldset>
                            <div class="form-group">
                                <label for="computerName">Computer name</label>
                                <input type="text" class="form-control" id="computerName" name="computerName" placeholder="Computer name" value="${computer.name}" required>
                            </div>
                            <div class="form-group">
                                <label for="introduced">Introduced date</label>
                                <input type="date" class="form-control" id="introduced" name="introduced" placeholder="Introduced date"
                                value="<javatime:format value="${computer.introduced}" pattern="yyyy-MM-dd" />">
                            </div>
                            <div class="form-group">
                                <label for="discontinued">Discontinued date</label>
                                <input type="date" class="form-control" id="discontinued" name="discontinued" placeholder="Discontinued date"
                                value="<javatime:format value='${computer.discontinued}' pattern='yyyy-MM-dd' />">
                            </div>
                            <div class="form-group">
                                <label for="companyId">Company</label>
                                <select class="form-control" id="companyId" name="companyId">
                                    <option value="0">--</option>
                                    <c:if test="${not empty computer.company}">
                                    <option value="${computer.company.id}" selected>${computer.company.name}</option>
                                    </c:if>
                                    <c:forEach var="company" items="${requestScope.companies}">
                                    <option value="${company.id}">${company.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </fieldset>
                        <div class="actions pull-right">
                            <input type="submit" value="Edit" class="btn btn-primary">
                            or
                            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-default">Cancel</a>
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
				<c:if test = "${updatedWithSuccess}">
                <div class="col-xs-8 col-xs-offset-2 box">
		            <div class="alert alert-info alert-dismissible fade in">
		                Computer information updated !
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