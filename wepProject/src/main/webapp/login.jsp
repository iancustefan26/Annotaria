<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: tud
  Date: 05.05.2025
  Time: 19:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>
<div class="container">
    <h2>Login</h2>
    <c:if test="${not empty param.message}">
        <p class="text-green-600">${param.message}</p>
    </c:if>
    <div id="message" class="message"></div>
    <form id="loginForm">
        <div>
            <label for="username">Username</label>
            <input type="text" id="username" required>
        </div>
        <div>
            <label for="password">Password</label>
            <input type="password" id="password" required>
        </div>
        <button type="submit">Login</button>
    </form>
    <p>Don't have an account? <a href="${pageContext.request.contextPath}/signup">Sign up</a></p>
</div>
<script src="${pageContext.request.contextPath}/js/auth.js"></script>
</body>
</html>