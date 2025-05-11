<%--
  Created by IntelliJ IDEA.
  User: tud
  Date: 05.05.2025
  Time: 19:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head><title>Welcome</title></head>
<body>
<h2>Welcome, <%= session.getAttribute("username") %>!</h2>
<a href="login.jsp">Logout</a>
</body>
</html>
