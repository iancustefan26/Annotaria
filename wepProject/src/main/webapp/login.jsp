<%--
  Created by IntelliJ IDEA.
  User: tud
  Date: 05.05.2025
  Time: 19:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Annotaria</title>

    <meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self' https://cdnjs.cloudflare.com; style-src 'self' https://cdnjs.cloudflare.com; img-src 'self' data:; font-src 'self' https://cdnjs.cloudflare.com; connect-src 'self'; frame-ancestors 'none';">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>
<div class="background-animation">
    <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
        <div class="shape shape-5"></div>
        <div class="shape shape-6"></div>
    </div>
</div>

<div class="auth-container">
    <div class="form-side">
        <div class="form-container">
            <div class="logo-section">
                <div class="logo-icon">
                    <img src="${pageContext.request.contextPath}/images/logo_without_background.png" alt="Annotaria Logo">
                </div>
                <h1 class="brand-name">Annotaria</h1>
            </div>

            <!-- Secure message display with proper escaping -->
            <c:if test="${not empty param.message}">
                <div class="message success-message">
                    <i class="fas fa-check-circle"></i>
                    <!-- Use fn:escapeXml to prevent XSS -->
                    <span><c:out value="${param.message}" escapeXml="true"/></span>
                </div>
            </c:if>

            <div id="message" class="message"></div>

            <form id="loginForm" class="auth-form">
                <!-- CSRF Token with proper escaping -->
                <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' escapeXml='true'/>">

                <div class="form-group">
                    <label for="username">
                        <i class="fas fa-user"></i>
                        Username
                    </label>
                    <input type="text"
                           id="username"
                           name="username"
                           required
                           placeholder="Enter your username"
                           pattern="[a-zA-Z0-9_]{1,20}"
                           title="Username must be 1-20 characters, letters, numbers, and underscores only"
                           autocomplete="username">
                </div>

                <div class="form-group">
                    <label for="password">
                        <i class="fas fa-lock"></i>
                        Password
                    </label>
                    <input type="password"
                           id="password"
                           name="password"
                           required
                           placeholder="Enter your password"
                           autocomplete="current-password">
                </div>

                <button type="submit" class="submit-btn">
                    <span>Sign In</span>
                    <i class="fas fa-arrow-right"></i>
                </button>
            </form>

            <div class="auth-switch">
                <p>Don't have an account?
                    <a href="${pageContext.request.contextPath}/signup" class="switch-link">Create Account</a>
                </p>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/auth.js"></script>
</body>
</html>