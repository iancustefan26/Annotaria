<%--
  Created by IntelliJ IDEA.
  User: tud
  Date: 5/18/25
  Time: 12:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Feed</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    <link rel="stylesheet" href="/wepProject_war_exploded/css/feed.css">
</head>
<body class="bg-gray-100">
<% if (session.getAttribute("userId") == null) {
    response.sendRedirect("login.jsp");
    return;
} %>
<div class="container mx-auto px-4 py-8">
    <!-- Navigation -->
    <div class="flex justify-center mb-6">
        <a href="/wepProject_war_exploded/profile" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
            Go to Profile
        </a>
    </div>

    <!-- Filter Controls -->
    <div class="mb-6 flex space-x-4 justify-center items-center">
        <select id="categoryFilter" class="border border-gray-300 rounded p-2">
            <option value="">All Categories</option>
            <!-- Populated dynamically by feed.js -->
        </select>
        <select id="yearFilter" class="border border-gray-300 rounded p-2">
            <option value="">All Years</option>
            <!-- Populated dynamically by feed.js -->
        </select>
        <select id="tagFilter" class="border border-gray-300 rounded p-2 w-64">
            <option value="">All Tags</option>
            <!-- Populated dynamically by feed.js -->
        </select>
    </div>

    <!-- Posts Container -->
    <div id="postsContainer" class="space-y-6">
        <c:if test="${empty posts}">
            <p class="text-gray-500 text-center">No posts available.</p>
        </c:if>
    </div>
</div>

<script src="/wepProject_war_exploded/js/social.js"></script>
<script src="/wepProject_war_exploded/js/feed.js"></script>
</body>
</html>