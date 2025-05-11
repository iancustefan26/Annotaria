<%--
  Created by IntelliJ IDEA.
  User: tud
  Date: 05.05.2025
  Time: 19:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign Up</title>
    <link rel="stylesheet" href="css/auth.css">
</head>
<body>
<div class="container">
    <h2>Sign Up</h2>
    <div id="message" class="message"></div>
    <form id="signupForm">
        <div>
            <label for="username">Username</label>
            <input type="text" id="username" required>
        </div>
        <div>
            <label for="email">Email</label>
            <input type="email" id="email" required>
        </div>
        <div>
            <label for="password">Password</label>
            <input type="password" id="password" required>
        </div>
        <button type="submit">Sign Up</button>
    </form>
    <p>Already have an account? <a href="login.jsp">Log in</a></p>
</div>
<script src="js/auth.js"></script>
</body>
</html>