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
    <link rel="stylesheet" href="css/auth.css">
</head>
<body>
<div class="container">
    <h2>Login</h2>
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
    <p>Don't have an account? <a href="signup.jsp">Sign up</a></p>
</div>
<script src="js/auth.js"></script>
</body>
</html>