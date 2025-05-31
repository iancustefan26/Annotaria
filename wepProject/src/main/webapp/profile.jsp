<%--
  Created by IntelliJ IDEA.
  User: tud
  Date: 5/18/25
  Time: 12:42 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="taglib" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Profile</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
  <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
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
      <h1 class="text-2xl font-bold">
        <taglib:choose>
          <taglib:when test="${profileUser != null}">
            <taglib:out value="${profileUser.username}"/>
          </taglib:when>
          <taglib:otherwise>
            <taglib:out value="${sessionScope.username}"/>
          </taglib:otherwise>
        </taglib:choose>
      </h1>
      <div class="mt-2">
        <span><strong>${posts.size()}</strong> posts</span>
      </div>
      <div class="mt-2">
        <a href="/wepProject_war_exploded/feed" class="text-blue-600 hover:underline">Feed</a> |
        <taglib:if test="${isOwnProfile}">
          <a href="/wepProject_war_exploded/logout" class="text-blue-600 hover:underline">Logout</a>
        </taglib:if>
        <taglib:if test="${!isOwnProfile}">
          <a href="/wepProject_war_exploded/profile" class="text-blue-600 hover:underline">My Profile</a>
        </taglib:if>
        <taglib:if test="${isOwnProfile && saved}">
          <a href="/wepProject_war_exploded/profile" class="text-blue-600 hover:underline">My Profile</a>
        </taglib:if>
      </div>
    </div>
  </div>

  <!-- Post Creation and Saved Posts Buttons -->
  <taglib:if test="${isOwnProfile && !saved}">
    <div class="mb-6">
      <button id="newPostBtn" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">New Post</button>
    </div>
    <div class="mb-6 flex space-x-4">
      <button id="deleteProfileBtn" class="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600">Delete Profile</button>
    </div>
    <taglib:if test="${posts.size() > 0}">
      <div class="mb-7 flex space-x-4">
        <button id="savedPostsBtn" class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600">Saved Posts</button>
      </div>
    </taglib:if>
  </taglib:if>

  <!-- Import/Export Buttons and Modals -->
  <taglib:if test="${isOwnProfile && saved}">
    <div class="mb-6">
      <button id="importBtn" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">Import</button>
    </div>
    <div class="mb-6 flex space-x-4">
      <button id="exportBtn" class="bg-green-500 text-white px-4 py-2 rounded hover:bg-blue-600">Export</button>
    </div>
    <!-- Import Modal -->
    <div id="importModal" class="modal">
      <div class="modal-content">
        <span class="close-modal">×</span>
        <h2 class="text-xl font-bold mb-4">Import Saved Posts</h2>
        <div id="importMessage" class="mb-4"></div>
        <form id="importForm" action="/wepProject_war_exploded/import-saved-posts" enctype="multipart/form-data" method="post">
          <div class="mb-4">
            <label for="importFile" class="block text-sm font-medium">Select JSON or XML File</label>
            <input type="file" id="importFile" name="importFile" accept=".json,.xml" required class="mt-1 block w-full border rounded p-2">
          </div>
          <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">Import</button>
        </form>
      </div>
    </div>
    <!-- Export Modal -->
    <div id="exportModal" class="modal">
      <div class="modal-content">
        <span class="close-modal">×</span>
        <h2 class="text-xl font-bold mb-4">Choose Export Format</h2>
        <div class="flex space-x-4 justify-center">
          <button id="exportJsonBtn" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">JSON</button>
          <button id="exportXmlBtn" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">XML</button>
        </div>
      </div>
    </div>
  </taglib:if>

  <!-- Section for displaying posts -->
  <h2 class="text-xl font-bold mb-4">
    <taglib:choose>
      <taglib:when test="${isOwnProfile}">
        <taglib:choose>
          <taglib:when test="${saved}">Your saved posts</taglib:when>
          <taglib:otherwise>Your posts</taglib:otherwise>
        </taglib:choose>
      </taglib:when>
      <taglib:otherwise>
        <taglib:out value="${profileUser.username}"/>'s Posts
      </taglib:otherwise>
    </taglib:choose>
  </h2>
  <div id="postsContainer" class="posts-container"></div>

  <!-- Post Grid (Fallback) -->
  <div class="post-grid mt-6">
    <taglib:forEach var="post" items="${posts}">
      <div>
        <a href="/wepProject_war_exploded/post?id=${post.id}">
          <taglib:choose>
            <taglib:when test="${not empty post.mediaBlobBase64}">
              <img src="${post.mediaBlobBase64}" alt="Post image" />
            </taglib:when>
            <taglib:otherwise>
              <div class="bg-gray-200 h-[150px] flex items-center justify-center">No Media</div>
            </taglib:otherwise>
          </taglib:choose>
        </a>
      </div>
    </taglib:forEach>
    <taglib:if test="${empty posts}">
      <p class="col-span-full text-center text-gray-500">No posts yet.</p>
    </taglib:if>
  </div>

  <!-- Post Creation Modal -->
  <taglib:if test="${isOwnProfile}">
    <div id="postModal" class="modal">
      <div class="modal-content">
        <span class="close-modal">×</span>
        <h2 class="text-xl font-bold mb-4">Create Post</h2>
        <div id="postMessage" class="mb-4"></div>
        <form id="postForm" action="/wepProject_war_exploded/import" enctype="multipart/form-data" method="post">
          <div class="mb-4">
            <label for="contentFile" class="block text-sm font-medium">Image</label>
            <input type="file" id="contentFile" name="contentFile" accept="image/*" required class="mt-1 block w-full border rounded p-2">
          </div>
          <div class="mb-4">
            <img id="previewImage" alt="Preview" class="hidden max-w-full h-auto"/>
          </div>
          <div class="mb-4">
            <label for="categoryId" class="block text-sm font-medium">Category</label>
            <select id="categoryId" name="categoryId" required class="mt-1 block w-full border rounded p-2">
              <option value="" disabled selected>Select a category</option>
              <taglib:forEach var="category" items="${categories}">
                <option value="${category.id}"><taglib:out value="${category.name}"/></option>
              </taglib:forEach>
            </select>
          </div>
          <div class="mb-4">
            <label for="namedTagIds" class="block text-sm font-medium">Named Tags</label>
            <select id="namedTagIds" name="namedTagIds[]" multiple class="mt-1 block w-full border rounded p-2">
              <!-- Populated dynamically by profile.js -->
            </select>
          </div>
          <div class="mb-4">
            <label for="userTaggedIds" class="block text-sm font-medium">Tag Users</label>
            <select id="userTaggedIds" name="userTaggedIds[]" multiple class="mt-1 block w-full border rounded p-2">
              <!-- Populated dynamically by profile.js -->
            </select>
          </div>
          <div class="mb-4">
            <label for="description" class="block text-sm font-medium">Description</label>
            <textarea id="description" name="description" rows="4" class="mt-1 block w-full border rounded p-2"></textarea>
          </div>
          <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">Post</button>
        </form>
      </div>
    </div>
  </taglib:if>
</div>

<script src="/wepProject_war_exploded/js/profile.js"></script>
</body>
</html>