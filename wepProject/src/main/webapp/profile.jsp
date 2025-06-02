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
<div class="max-w-4xl mx-auto p-12">
  <!-- Profile Header -->
  <div class="profile-wrapper">
    <div class="flex items-center mb-6 profile-header">
      <div class="w-24 h-24 avatar-placeholder rounded-full mr-4" data-initials=""></div>
      <div class="profile-info">
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
        <div class="mt-2 profile-stats">
          <span><strong>${post.size()}</strong> Posts</span>
        </div>
        <div class="mt-2 profile-links">
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
      <div class="mb-6 btn-group">
        <button id="newPostBtn" class="action-btn">New Post</button>
      </div>
      <div class="mb-6 btn-group">
        <button id="deleteProfileBtn" class="action-btn delete-btn">Delete Profile</button>
      </div>
      <taglib:if test="${posts.size() > 0}">
        <div class="mb-6 btn-group">
          <button id="savedPostsBtn" class="action-btn saved-btn">Saved Posts</button>
        </div>
      </taglib:if>
    </taglib:if>

    <!-- Import/Export Buttons and Modals -->
    <taglib:if test="${isOwnProfile && saved}">
      <div class="mb-6 btn-group">
        <button id="importBtn" class="action-btn import-btn">Import</button>
      </div>
      <div class="mb-6 btn-group">
        <button id="exportBtn" class="action-btn export-btn">Export</button>
      </div>
      <!-- Import Modal -->
      <div id="importModal" class="modal">
        <div class="modal-content">
          <span class="close-modal">&times;</span>
          <h2 class="text-xl font-bold mb-4">Import Saved Posts</h2>
          <div id="importMessage" class="modal-message"></div>
          <form id="importForm" class="modal-form" action="/wepProject_war_exploded/import-saved-posts" enctype="multipart/form-data" method="post">
            <div class="form-group">
              <label for="importFile">Select JSON or XML File</label>
              <input type="file" id="importFile" name="importFile" accept=".json,.xml" required>
            </div>
            <button type="submit" class="form-submit">Import</button>
          </form>
        </div>
      </div>
      <!-- Export Modal -->
      <div id="exportModal" class="modal">
        <div class="modal-content">
          <span class="close-modal">&times;</span>
          <h2 class="text-xl font-bold mb-4">Choose Export Format</h2>
          <div class="btn-group">
            <button id="exportJsonBtn" class="action-btn">JSON</button>
            <button id="exportXmlBtn" class="action-btn">XML</button>
          </div>
        </div>
      </div>
    </taglib:if>

    <!-- Section for displaying posts -->
    <h2 class="text-xl font-bold mb-4 posts-title">
      <taglib:choose>
        <taglib:when test="${isOwnProfile}">
          <taglib:choose>
            <taglib:when test="${saved}">Your Saved Posts</taglib:when>
            <taglib:otherwise>Your Posts</taglib:otherwise>
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
        <div class="post-item">
          <a href="/wepProject_war_exploded/post?id=${post.id}">
            <taglib:choose>
              <taglib:when test="${post.mediaType == 'video'}">
                <video class="w-full h-[150px] object-cover">
                  <source src="${post.mediaBlobBase64}" type="video/mp4">
                </video>
              </taglib:when>
              <taglib:otherwise>
                <img src="${post.mediaBlobBase64}" alt="Post image" />
              </taglib:otherwise>
            </taglib:choose>
          </a>
        </div>
      </taglib:forEach>
      <taglib:if test="${empty posts}">
        <p class="no-posts">No posts yet.</p>
      </taglib:if>
    </div>

    <!-- Post Creation Modal -->
    <taglib:if test="${isOwnProfile}">
      <div id="postModal" class="modal">
        <div class="modal-content">
          <span class="close-modal">&times;</span>
          <h2 class="text-xl font-bold mb-4">Create Post</h2>
          <div id="postMessage" class="modal-message"></div>
          <form id="postForm" class="modal-form" action="/wepProject_war_exploded/import" enctype="multipart/form-data" method="post">
            <div class="form-group">
              <label for="contentFile">Image or Video</label>
              <input type="file" id="contentFile" name="contentFile" accept="image/*,video/mp4,video/quicktime" required>
            </div>
            <div class="form-group">
              <img id="previewImage" alt="Preview" class="hidden" />
              <video id="previewVideo" controls class="hidden"></video>
            </div>
            <div class="form-group">
              <label for="categoryId">Category</label>
              <select id="categoryId" name="categoryId" required>
                <option value="" disabled selected>Select a category</option>
                <taglib:forEach var="category" items="${categories}">
                  <option value="${category.id}"><taglib:out value="${category.name}"/></option>
                </taglib:forEach>
              </select>
            </div>
            <div class="form-group">
              <label for="namedTagIds">Named Tags</label>
              <select id="namedTagIds" name="namedTagIds[]" multiple></select>
            </div>
            <div class="form-group">
              <label for="userTaggedIds">Tag Users</label>
              <select id="userTaggedIds" name="userTaggedIds[]" multiple></select>
            </div>
            <div class="form-group">
              <label for="description">Description</label>
              <textarea id="description" name="description" rows="4"></textarea>
            </div>
            <button type="submit" class="form-submit">Post</button>
          </form>
        </div>
      </div>
    </taglib:if>
  </div>
</div>
  <script src="/wepProject_war_exploded/js/profile.js"></script>
</body>
</html>