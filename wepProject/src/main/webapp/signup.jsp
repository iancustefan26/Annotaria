<%--
  Created by IntelliJ IDEA.
  User: tud
  Date: 05.05.2025
  Time: 19:21
  To change this template use File | Settings | File Templates.
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign Up - Annotaria</title>
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

    <!-- Main Container -->
    <div class="auth-container">
        <!-- Registration Form -->
        <div class="form-side">
            <div class="form-container">
                <div class="logo-section">
                    <div class="logo-icon">
                        <img src="${pageContext.request.contextPath}/images/logo_without_background.png" alt="Annotaria Logo">
                    </div>
                    <h1 class="brand-name">Annotaria</h1>
                </div>

                <div class="form-header">
                    <h2>Create Account</h2>
                </div>

                <div id="message" class="message"></div>

                <form id="signupForm" class="auth-form">
                    <div class="form-group">
                        <label for="username">
                            <i class="fas fa-user"></i>
                            Username
                        </label>
                        <input type="text" id="username" required placeholder="Choose a username">
                    </div>

                    <div class="form-group">
                        <label for="email">
                            <i class="fas fa-envelope"></i>
                            Email
                        </label>
                        <input type="email" id="email" required placeholder="Enter your email">
                    </div>

                    <div class="form-group">
                        <label for="password">
                            <i class="fas fa-lock"></i>
                            Password
                        </label>
                        <div class="password-input-container">
                            <input type="password" id="password" required placeholder="Create a password">
                            <button type="button" class="password-toggle" onclick="togglePassword('password')">
                                <i class="fas fa-eye"></i>
                            </button>
                        </div>
                    </div>

                    <button type="submit" class="submit-btn">
                        <span>Create Account</span>
                        <i class="fas fa-rocket"></i>
                    </button>
                </form>

                <!-- Sign In Link -->
                <div class="auth-switch">
                    <p>Already have an account?
                        <a href="${pageContext.request.contextPath}/login" class="switch-link">Sign In</a>
                    </p>
                </div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/auth.js"></script>
    <script>

    </script>
</body>
</html>
