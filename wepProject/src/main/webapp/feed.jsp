<%--
  Created by IntelliJ IDEA.
  User: tud
  Date: 5/18/25
  Time: 12:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <link rel="https://cdn.jsdelivr.net/npm/font-awesome@4.7.0/css/font-awesome.min.css" rel="sha256-eZrrJcwDc/3uDhsdt61sL2oOBY362qM3lon1gyExkL0=" crossorigin="anonymous"></link>
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link rel="stylesheet" href="/wepProject_war_exploded/css/feed.css">
</head>
<body class="bg-gray-100">
<% if (session.getAttribute("userId") == null) {
    response.sendRedirect("index.html");
    return;
} %>
<div class="container mx-auto px-4 py-8">
    <div class="fixed-filter-container fixed top-0 left-0 right-0 bg-gray-100 z-10 pt-4 pb-2 shadow-md">
        <div class="flex flex-col space-y-2 max-w-4xl mx-auto px-4">
            <div class="flex justify-center space-x-4 items-center
                <div class="flex items-center">
            <a href="/wepProject_war_exploded/profile" class="bg-blue-600 text-white px-3 py-2 rounded-lg hover:bg-blue-500">
                Go to Profile
            </a>
        </div>
        <div class="relative flex items-center">
            <button id="exportStatisticsBtn" class="bg-blue-600 text-white px-3 py-2 rounded-lg hover:bg-blue-500 flex items-center">
                <i class="fas fa-solid fa-download mr-2"></i> Export Statistics
            </button>
            <div id="exportDropdown" class="hidden absolute top-full mt-2 right-0 bg-white border border-gray-300 rounded shadow-lg z-20">
                <a href="#" id="exportCsv" class="block px-4 py-2 text-gray-700 hover:bg-gray-100">CSV</a>
                <a href="#" id="exportSvg" class="block px-4 py-2 text-gray-700 hover:bg-gray-100">SVG</a>
            </div>
        </div>
    </div>
    <div class="flex space-x-4 justify-center items-center">
        <select id="categoryFilter" class="border border-gray-300 rounded p-2 bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500">
            <option value="">All Categories</option>
        </select>
        <select id="yearFilter" class="border border-gray-300 rounded p-2 bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500">
            <option value="">All Years</option>
        </select>
        <select id="tagFilter" class="border border-gray-300 rounded p-2 bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500">
            <option value="">All Tags</option>
        </select>
        <div class="relative flex-1">
            <input id="userSearch" type="text" placeholder="Search users..." class="w-full border border-gray-300 rounded p-2 bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 pl-10">
            <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
        </div>
    </div>
</div>
</div>

<div class="h-24"></div>

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