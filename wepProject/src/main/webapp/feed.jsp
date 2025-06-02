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
    <link rel="stylesheet" href="/wepProject_war_exploded/css/post.css">
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
            <c:choose>
            <c:when test="${empty posts}">
            <div class="text-center py-12">
                <div class="bg-white rounded-xl shadow-sm p-8 max-w-md mx-auto">
                    <i class="fas fa-inbox text-4xl text-gray-300 mb-4"></i>
                    <p class="text-gray-500 text-lg">No posts available</p>
                    <p class="text-gray-400 text-sm mt-2">Posts will appear here when they become available</p>
                </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="post" items="${posts}">
                        <div class="bg-white rounded-lg shadow-md max-w-xl mx-auto mb-8" data-post-id="${post.id}">
                            <div class="flex items-center p-4 border-b">
                                <div class="w-8 h-8 rounded-full avatar-placeholder mr-3" data-initials="${post.authorUsername != null ? post.authorUsername.substring(0, 2).toUpperCase() : 'UN'}"></div>
                                <div class="flex-1">
                                    <p class="font-semibold">${post.authorUsername != null ? post.authorUsername : 'User #' + post.authorId}</p>
                                </div>
                                <div class="flex items-center space-x-2">
                                    <c:if test="${post.isOwnPost}">
                                        <button class="deleteButton focus:outline-none text-red-500 hover:text-red-700" data-post-id="${post.id}" title="Delete Post">
                                            <i class="fas fa-trash-alt"></i>
                                        </button>
                                    </c:if>
                                    <div class="text-gray-500">
                                        <i class="fas fa-ellipsis-h"></i>
                                    </div>
                                </div>
                            </div>
                            <div class="post-media relative">
                                <c:choose>
                                    <c:when test="${not empty post.mediaBlobBase64}">
                                        <c:choose>
                                            <c:when test="${post.mediaType == 'video'}">
                                                <video controls class="w-full object-cover max-h-[400px]">
                                                    <source src="${post.mediaBlobBase64}" type="video/mp4">
                                                    Your browser does not support the video tag.
                                                </video>
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${post.mediaBlobBase64}" alt="Post" class="w-full object-cover" />
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:when test="${not empty post.externalMediaUrl}">
                                        <c:choose>
                                            <c:when test="${post.mediaType == 'video'}">
                                                <video controls class="w-full object-cover max-h-[400px]">
                                                    <source src="${post.externalMediaUrl}" type="video/mp4">
                                                    Your browser does not support the video tag.
                                                </video>
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${post.externalMediaUrl}" alt="Post" class="w-full object-cover" />
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="bg-gray-200 h-[400px] flex items-center justify-center">
                                            <span class="text-gray-500">No Media</span>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="p-4">
                                <div class="flex space-x-4 mb-2">
                                    <button class="likeButton focus:outline-none" data-post-id="${post.id}">
                                        <i class="${post.isLiked ? 'fas text-red-500' : 'far'} fa-heart text-2xl"></i>
                                    </button>
                                    <button class="commentButton focus:outline-none" data-post-id="${post.id}">
                                        <i class="far fa-comment text-2xl"></i>
                                    </button>
                                    <button class="saveButton focus:outline-none" data-post-id="${post.id}">
                                        <i class="${post.isSaved ? 'fas' : 'far'} fa-bookmark text-2xl"></i>
                                    </button>
                                    <div class="flex-grow"></div>
                                </div>
                                <div class="mb-2">
                                    <p class="font-semibold"><span class="likesNumber">${post.likeCount}</span> likes</p>
                                </div>
                                <div class="mb-3">
                                    <p>
                                        <span class="font-semibold">${post.authorUsername != null ? post.authorUsername : 'User #' + post.authorId}</span>
                                        <span>${post.description}</span>
                                    </p>
                                </div>
                                <div class="text-gray-500 text-xs mb-3">
                                        ${post.datePosted.toString()}
                                    <c:if test="${not empty post.creationYear}"> · Created in ${post.creationYear}</c:if>
                                    <c:if test="${not empty post.categoryId}"> · Category: ${categoryNames[post.categoryId]}</c:if>
                                </div>
                                <p class="text-gray-500 text-sm mb-2">
                                    <span class="commentCount">${post.commentCount}</span> comments
                                </p>
                                <div class="commentsContainer max-h-60 overflow-y-auto mb-3 hidden" data-post-id="${post.id}">
                                    <!-- Comments loaded here -->
                                </div>
                                <div class="border-t pt-3">
                                    <div class="flex">
                                        <textarea class="commentInput flex-grow border-none bg-transparent focus:outline-none resize-none" placeholder="Add a comment..." rows="1" data-post-id="${post.id}"></textarea>
                                        <button class="submitComment text-blue-500 font-semibold ml-2" data-post-id="${post.id}">Post</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<script src="/wepProject_war_exploded/js/social.js"></script>
<script src="/wepProject_war_exploded/js/feed.js"></script>
</body>
</html>