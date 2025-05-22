<%--
  Created by IntelliJ IDEA.
  User: tud
  Date: 5/18/25
  Time: 12:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Social Media Feed</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body class="bg-gray-100">
<div class="container mx-auto px-4 py-8">
    <h1 class="text-2xl font-bold mb-6 text-center">Social Media Feed</h1>

    <c:if test="${not empty error}">
        <p class="text-red-500 text-center">${error}</p>
    </c:if>

    <!-- Posts Container -->
    <div id="postsContainer" class="space-y-6">
        <!-- Posts loaded via JavaScript -->
    </div>

    <!-- Navigation -->
    <div class="flex justify-center mt-6">
        <a href="wepProject_war_exploded/profile" class="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600">
            Go to Profile
        </a>
    </div>
</div>

<script src="js/feed.js"></script>
</body>
</html>