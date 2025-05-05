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
<head><title>Sign Up</title></head>
<body>
<h2>Sign Up</h2>
<% if (request.getParameter("error") != null && request.getParameter("error").equals("exists")) { %>
<p style="color:red">Username already exists!</p>
<% } %>
<% if (request.getParameter("signup") != null && request.getParameter("signup").equals("success")) { %>
<p style="color:green">Sign up successful! Please log in.</p>
<% } %>
<form action="signup" method="post">
    <label>Username: <input type="text" name="username" required></label><br>
    <label>Password: <input type="password" name="password" required></label><br>
    <input type="submit" value="Sign Up">
</form>
<p>Already have an account? <a href="login.jsp">Log in</a></p>
</body>
</html>
