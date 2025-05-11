<%--
  Created by IntelliJ IDEA.
  User: tud
  Date: 05.05.2025
  Time: 19:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sign Up</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 400px; margin: 50px auto; }
        p { margin: 10px 0; }
        form { display: flex; flex-direction: column; gap: 10px; }
        input { padding: 8px; }
        input[type="submit"] { background-color: #28a745; color: white; border: none; cursor: pointer; }
        input[type="submit"]:hover { background-color: #218838; }
    </style>
</head>
<body>
<h2>Sign Up</h2>
<% if (request.getParameter("error") != null && request.getParameter("error").equals("exists")) { %>
<p style="color:red">Username or email already exists!</p>
<% } %>
<% if (request.getParameter("signup") != null && request.getParameter("signup").equals("success")) { %>
<p style="color:green">Sign up successful! Please log in.</p>
<% } %>
<form action="signup" method="post">
    <label>Username: <input type="text" name="username" required></label>
    <label>Email: <input type="email" name="email" required></label>
    <label>Password: <input type="password" name="password" required></label>
    <input type="submit" value="Sign Up">
</form>
<p>Already have an account? <a href="login.jsp">Log in</a></p>
</body>
</html>
