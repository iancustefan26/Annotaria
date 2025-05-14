<%--
  Created by IntelliJ IDEA.
  User: tud
  Date: 05.05.2025
  Time: 19:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h2>Login</h2>
<% if (request.getParameter("error") != null && request.getParameter("error").equals("invalid")) { %>
<p style="color:red">Invalid username or password!</p>
<% } %>
<% if (request.getParameter("error") != null && request.getParameter("error").equals("db")) { %>
<p style="color:red">Database error, please try again later!</p>
<% } %>
<form action="login" method="post">
    <label>Username: <input type="text" name="username" required></label><br>
    <label>Password: <input type="password" name="password" required></label><br>
    <input type="submit" value="Login">
</form>
<p>Don't have an account? <a href="signup.jsp">Sign up</a></p>
</body>
</html>
