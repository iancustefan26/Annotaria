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
<body class="bg-gradient-to-br from-gray-50 to-gray-100 min-h-screen">
<% if (session.getAttribute("userId") == null) {
    response.sendRedirect("login.jsp");
    return;
} %>

<div class="container mx-auto px-4 py-8">
    <div class="fixed-filter-container">
        <div class="max-w-4xl mx-auto px-4">
            <div class="flex justify-between items-center">
                <div class="relative">
                    <button id="exportStatisticsBtn" class="flex items-center gap-2">
                        <i class="fas fa-download"></i>
                        <span>Export Stats</span>
                    </button>
                    <div id="exportDropdown" class="hidden absolute top-full mt-2 left-0 z-20">
                        <a href="#" id="exportCsv" class="block">
                            <i class="fas fa-file-csv mr-2"></i>CSV
                        </a>
                        <a href="#" id="exportSvg" class="block">
                            <i class="fas fa-file-image mr-2"></i>SVG
                        </a>
                    </div>
                </div>

                <div class="relative flex-1 max-w-sm mx-4">
                    <input id="userSearch" type="text" placeholder="Search users..." class="w-full">
                    <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2"></i>
                </div>

                <a href="/wepProject_war_exploded/profile" class="flex items-center gap-2">
                    <i class="fas fa-user"></i>
                    <span>Profile</span>
                </a>
            </div>
        </div>
    </div>

    <!-- Left Sidebar: Filters -->
    <div class="fixed left-0 w-48">
        <div class="flex flex-col space-y-4">
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Category</label>
                <select id="categoryFilter">
                    <option value="">All Categories</option>
                </select>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Year</label>
                <select id="yearFilter">
                    <option value="">All Years</option>
                </select>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Tags</label>
                <select id="tagFilter">
                    <option value="">All Tags</option>
                </select>
            </div>
        </div>
    </div>

    <div class="ml-48 pt-16">
        <div id="postsContainer" class="space-y-6 min-h-screen">
            <c:if test="${empty posts}">
                <div class="text-center py-12">
                    <div class="bg-white rounded-xl shadow-sm p-8 max-w-md mx-auto">
                        <i class="fas fa-inbox text-4xl text-gray-300 mb-4"></i>
                        <p class="text-gray-500 text-lg">No posts available</p>
                        <p class="text-gray-400 text-sm mt-2">Posts will appear here when they become available</p>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</div>

<script src="/wepProject_war_exploded/js/social.js"></script>
<script src="/wepProject_war_exploded/js/feed.js"></script>
</body>
</html>