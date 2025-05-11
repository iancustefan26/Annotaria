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
    <style>
        body { font-family: Arial, sans-serif; max-width: 400px; margin: 50px auto; }
        .error { color: red; }
        .success { color: green; }
        form { display: flex; flex-direction: column; gap: 10px; }
        input { padding: 8px; }
        button { padding: 10px; background-color: #007bff; color: white; border: none; cursor: pointer; }
        button:hover { background-color: #0056b3; }
    </style>
</head>
<body>
<h2>Login</h2>
<div id="message"></div>
<form id="loginForm">
    <label>Username: <input type="text" id="username" required></label>
    <label>Password: <input type="password" id="password" required></label>
    <button type="submit">Login</button>
</form>
<p>Don't have an account? <a href="signup.jsp">Sign up</a></p>

<script>
    document.getElementById("loginForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;
        const messageDiv = document.getElementById("message");

        try {
            const response = await fetch("login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, password })
            });
            const result = await response.json();

            if (result.status === "success") {
                messageDiv.innerHTML = `<p class="success">${result.message}</p>`;
                window.location.href = "welcome.jsp";
            } else {
                messageDiv.innerHTML = `<p class="error">${result.message}</p>`;
            }
        } catch (error) {
            messageDiv.innerHTML = `<p class="error">Network error, please try again</p>`;
        }
    });
</script>
</body>
</html>