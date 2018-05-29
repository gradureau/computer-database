<!DOCTYPE html>
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
            <div class="alert alert-danger">
                <spring:message code="not_found_404"/>
                <br/>
                <!-- stacktrace -->
            </div>
        </div>
    </section>

    <script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/dashboard.js"></script>

</body>
</html>