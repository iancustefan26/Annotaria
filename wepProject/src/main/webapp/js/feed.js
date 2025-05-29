document.addEventListener('DOMContentLoaded', () => {
    function loadPosts() {
        $.ajax({
            url: '/wepProject_war_exploded/feed',
            type: 'GET',
            headers: { 'Accept': 'application/json' },
            success: function(response) {
                if (response.status === 'success') {
                    const postsContainer = $('#postsContainer');
                    postsContainer.empty();

                    if (response.data.length === 0) {
                        postsContainer.append('<p class="text-gray-500 text-center">No posts available.</p>');
                        return;
                    }

                    response.data.forEach(post => {
                        const postHtml = `
              <div class="bg-white rounded-lg shadow-md max-w-xl mx-auto mb-8">
                <div class="flex items-center p-4 border-b">
                  <div class="w-8 h-8 bg-gray-300 rounded-full mr-3"></div>
                  <div class="flex-1">
                    <p class="font-semibold">${post.authorUsername || 'User #' + post.authorId}</p>
                  </div>
                  ${post.isOwnPost ? `
                    <div class="flex items-center space-x-2">
                      <button class="deleteButton focus:outline-none text-red-500 hover:text-red-700" data-post-id="${post.id}" title="Delete Post">
                        <i class="fas fa-trash-alt"></i>
                      </button>
                      <div class="text-gray-500">
                        <i class="fas fa-ellipsis-h"></i>
                      </div>
                    </div>
                  ` : `
                    <div class="text-gray-500">
                      <i class="fas fa-ellipsis-h"></i>
                    </div>
                  `}
                </div>
                <div class="post-media relative">
                  ${post.mediaBlobBase64 ? `
                    <a href="/wepProject_war_exploded/post?id=${post.id}">
                      <img src="${post.mediaBlobBase64}" alt="Post" class="w-full object-cover" />
                    </a>
                  ` : post.externalMediaUrl ? `
                    <a href="/wepProject_war_exploded/post?id=${post.id}">
                      <img src="${post.externalMediaUrl}" alt="Post" class="w-full object-cover" />
                    </a>
                  ` : `
                    <div class="bg-gray-200 h-[400px] flex items-center justify-center">
                      <span class="text-gray-500">No Media</span>
                    </div>
                  `}
                </div>
                <div class="p-4">
                  <div class="flex space-x-4 mb-2">
                    <button class="likeButton focus:outline-none" data-post-id="${post.id}">
                      <i class="far fa-heart text-2xl"></i>
                    </button>
                    <button class="commentButton focus:outline-none" onclick="window.location.href='/wepProject_war_exploded/post?id=${post.id}'">
                      <i class="far fa-comment text-2xl"></i>
                    </button>
                    <div class="flex-grow"></div>
                  </div>
                  <div class="mb-2">
                    <p class="font-semibold"><span class="likesNumber">${post.likeCount}</span> likes</p>
                  </div>
                  <div class="mb-3">
                    <p>
                      <span class="font-semibold">${post.authorUsername || 'User #' + post.authorId}</span>
                      <span>${post.description}</span>
                    </p>
                  </div>
                  <div class="text-gray-500 text-xs mb-3">
                    ${new Date(post.datePosted).toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' })}
                    ${post.creationYear ? ` · Created in ${post.creationYear}` : ''}
                    ${post.categoryId ? ` · Category ID: ${post.categoryId}` : ''}
                  </div>
                  <p class="text-gray-500 text-sm mb-2">
                    <span class="commentCount">${post.commentCount}</span> comments
                  </p>
                </div>
              </div>
            `;
                        postsContainer.append(postHtml);
                    });

                    $('.deleteButton').on('click', function() {
                        const postId = $(this).data('post-id');
                        if (confirm('Are you sure you want to delete this post?')) {
                            $.ajax({
                                url: `/wepProject_war_exploded/post?id=${postId}`,
                                type: 'DELETE',
                                headers: { 'Accept': 'application/json' },
                                success: function(response) {
                                    if (response.status === 'success') {
                                        alert('Post deleted successfully');
                                        loadPosts();
                                    } else {
                                        alert(response.message || 'Failed to delete post');
                                    }
                                },
                                error: function(xhr) {
                                    alert(xhr.responseJSON?.message || 'Error deleting post');
                                }
                            });
                        }
                    });
                } else {
                    $('#postsContainer').html('<p class="text-red-500 text-center">' + response.message + '</p>');
                }
            },
            error: function(xhr) {
                $('#postsContainer').html('<p class="text-red-500 text-center">Failed to load posts: ' + (xhr.responseJSON?.message || 'Server error') + '</p>');
            }
        });
    }

    loadPosts();
});