<%--
  Created by IntelliJ IDEA.
  User: tud
  Date: 5/20/25
  Time: 5:25 PM
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
  <title>Post Details</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <link rel="stylesheet" href="/wepProject_war_exploded/css/feed.css">
</head>
<body class="bg-gray-100">
<div class="container mx-auto px-4 py-8">
  <c:if test="${not empty error}">
    <p class="text-red-500 text-center">${error}</p>
  </c:if>
  <c:if test="${empty post}">
    <p class="text-red-500 text-center">No post data available.</p>
  </c:if>
  <c:if test="${not empty post}">
    <!-- Posts Container -->
    <div id="postsContainer" class="space-y-6">
      <!-- Instagram-like Post Card -->
      <div class="bg-white rounded-lg shadow-md max-w-xl mx-auto mb-8" data-post-id="${post.id}">
        <!-- Post Header -->
        <div class="flex items-center p-4 border-b">
          <div class="w-8 h-8 bg-gray-300 rounded-full mr-3"></div>
          <div class="flex-1">
            <p class="font-semibold">${post.authorUsername != null ? post.authorUsername : 'User #' + post.authorId}</p>
          </div>
          <div class="flex items-center space-x-2">
            <c:if test="${isOwnProfile}">
              <button id="deleteButton" class="focus:outline-none text-red-500 hover:text-red-700" data-post-id="${post.id}" title="Delete Post">
                <i class="fas fa-trash-alt"></i>
              </button>
            </c:if>
            <div class="text-gray-500">
              <i class="fas fa-ellipsis-h"></i>
            </div>
          </div>
        </div>

        <!-- Post Media -->
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

        <!-- Engagement Actions -->
        <div class="p-4">
          <div class="flex space-x-4 mb-2">
            <button id="likeButton" class="likeButton focus:outline-none" data-post-id="${post.id}">
              <i id="likeIcon" class="${post.isLiked != null && post.isLiked ? 'fas text-red-500' : 'far'} fa-heart text-2xl"></i>
            </button>
            <button id="commentButton" class="commentButton focus:outline-none" data-post-id="${post.id}">
              <i class="far fa-comment text-2xl"></i>
            </button>
            <button id="saveButton" class="saveButton focus:outline-none" data-post-id="${post.id}">
              <i id="saveIcon" class="${post.isSaved != null && post.isSaved ? 'fas' : 'far'} fa-bookmark text-2xl"></i>
            </button>
            <div class="flex-grow"></div>
          </div>

          <!-- Like Count -->
          <div class="mb-2">
            <p id="likeCount" class="font-semibold"><span class="likesNumber">${post.likeCount}</span> likes</p>
          </div>

          <!-- Post Caption -->
          <div class="mb-3">
            <p>
              <span class="font-semibold">${post.authorUsername != null ? post.authorUsername : 'User #' + post.authorId}</span>
              <span>${post.description}</span>
            </p>
          </div>

          <!-- Post Metadata -->
          <div class="text-gray-500 text-xs mb-3">
            <fmt:formatDate value="${post.datePosted}" pattern="MMMM dd, yyyy" />
            <c:if test="${not empty post.creationYear}">
              · Created in ${post.creationYear}
            </c:if>
            <c:if test="${not empty post.categoryId}">
              · Category name: ${categoryName}
            </c:if>
          </div>

          <!-- Comments Indicator -->
          <p class="text-gray-500 text-sm mb-2">
            <span class="commentCount">${post.commentCount}</span> comments
          </p>

          <!-- Comments Section -->
          <div id="commentsContainer" class="commentsContainer max-h-60 overflow-y-auto" data-post-id="${post.id}">
            <!-- Comments loaded via JavaScript -->
          </div>

          <!-- Comment Form -->
          <div class="mt-3 border-t pt-3">
            <div class="flex">
              <textarea id="commentInput" class="commentInput flex-grow border-none bg-transparent focus:outline-none resize-none" placeholder="Add a comment..." rows="1" data-post-id="${post.id}"></textarea>
              <button id="submitComment" class="submitComment text-blue-500 font-semibold ml-2" data-post-id="${post.id}">Post</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Navigation -->
    <div class="flex justify-center">
      <a href="/wepProject_war_exploded/feed" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
        Back to Feed
      </a>
      <a href="/wepProject_war_exploded/profile" class="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600 ml-3">
        Go to Profile
      </a>
    </div>
  </c:if>
</div>

<script src="/wepProject_war_exploded/js/social.js"></script>
<script src="/wepProject_war_exploded/js/post.js"></script>
</body>
</html>