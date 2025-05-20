<%--
  Created by IntelliJ IDEA.
  User: tud
  Date: 5/18/25
  Time: 12:42 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Profile</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="stylesheet" href="/wepProject_war_exploded/css/profile.css">
</head>
<body class="bg-gray-100">
<% if (session.getAttribute("userId") == null) {
  response.sendRedirect("login.jsp");
  return;
} %>
<div class="max-w-4xl mx-auto p-4">
  <!-- Profile Header -->
  <div class="flex items-center mb-6">
    <div class="w-24 h-24 bg-gray-300 rounded-full mr-4"></div>
    <div>
      <h1 class="text-2xl font-bold"><c:out value="${sessionScope.username}"/></h1>
      <div class="mt-2">
        <span><strong>${postCount}</strong> posts</span>
      </div>
      <div class="mt-2">
        <a href="/wepProject_war_exploded/feed.jsp" class="text-blue-600 hover:underline">Feed</a> |
        <a href="/wepProject_war_exploded/logout" class="text-blue-600 hover:underline">Logout</a>
      </div>
    </div>
  </div>

  <!-- Post Creation -->
  <div class="mb-6">
    <button id="newPostBtn" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">New Post</button>
  </div>

  <!-- Section for displaying posts -->
  <h2 class="text-xl font-bold mb-4">Your Posts</h2>
  <div id="postsContainer" class="posts-container"></div>

  <!-- Post Grid (Fallback) -->
  <div class="post-grid mt-6">
    <c:forEach var="post" items="${posts}">
      <div>
        <a href="/wepProject_war_exploded/post?id=${post.id}">
          <c:choose>
            <c:when test="${not empty post.mediaBlobBase64}">
              <img src="${post.mediaBlobBase64}"  />
            </c:when>
            <c:otherwise>
              <div class="bg-gray-200 h-[150px] flex items-center justify-center">No Media</div>
            </c:otherwise>
          </c:choose>
        </a>
      </div>
    </c:forEach>
    <c:if test="${empty posts}">
      <p class="col-span-full text-center text-gray-500">No posts yet.</p>
    </c:if>
  </div>

  <!-- Post Creation Modal -->
  <div id="postModal" class="modal">
    <div class="modal-content">
      <span class="close-modal">×</span>
      <h2 class="text-xl font-bold mb-4">Create Post</h2>
      <div id="postMessage" class="mb-4"></div>
      <form id="postForm" enctype="multipart/form-data" action="/wepProject_war_exploded/import" method="post">
        <div class="mb-4">
          <label for="contentFile" class="block text-sm font-medium">Image</label>
          <input type="file" id="contentFile" name="contentFile" accept="image/*" required class="mt-1 block w-full border rounded p-2">
        </div>
        <div class="mb-4">
          <img id="previewImage" alt="Preview" />
        </div>
        <div class="mb-4">
          <label for="description" class="block text-sm font-medium">Description</label>
          <textarea id="description" name="description" rows="4" class="mt-1 block w-full border rounded p-2"></textarea>
        </div>
        <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">Post</button>
      </form>
    </div>
  </div>
</div>

<script src="/wepProject_war_exploded/js/profile.js"></script>
</body>
</html>