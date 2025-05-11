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
        .error { color: red; }
        .success { color: green; }
        form { display: flex; flex-direction: column; gap: 10px; }
        input { padding: 8px; }
        button { padding: 10px; background-color: #28a745; color: white; border: none; cursor: pointer; }
        button:hover { background-color: #218838; }
    </style>
</head>
<body>
<h2>Sign Up</h2>
<div id="message"></div>
<form id="signupForm">
    <label>Username: <input type="text" id="username" required></label>
    <label>Email: <input type="email" id="email" required></label>
    <label>Password: <input type="password" id="password" required></label>
    <button type="submit">Sign Up</button>
</form>
<p>Already have an account? <a href="login.jsp">Log in</a></p>

<script>
    document.getElementById("signupForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        const username = document.getElementById("username").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        const messageDiv = document.getElementById("message");

        try {
            const response = await fetch("signup", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, email, password })
            });
            const result = await response.json();

            if (result.status === "success") {
                messageDiv.innerHTML = `<p class="success">${result.message}</p>`;
                window.location.href = "login.jsp";
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