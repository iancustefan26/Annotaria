$(document).ready(function() {
    const contextPath = '<%= request.getContextPath() %>';

    function loadPosts() {
        $.ajax({
            url: `${contextPath}/feed`,
            type: 'GET',
            headers: {
                'Accept': 'application/json'
            },
            success: function(response) {
                console.log('Feed response:', response); // Debug
                if (response.status === 'success' && Array.isArray(response.data)) {
                    const posts = response.data;

                    // Clear existing posts
                    $('#postsContainer').empty();

                    // Add posts to container
                    if (posts.length > 0) {
                        posts.forEach(post => {
                            const postHtml = createPostElement(post);
                            $('#postsContainer').append(postHtml);
                        });
                    } else {
                        $('#postsContainer').append('<p class="text-gray-500 text-center py-4">No posts available</p>');
                    }
                }
            },
            error: function(xhr) {
                console.error('Feed load error:', xhr.status, xhr.responseJSON); // Debug
                const response = xhr.responseJSON;
                $('#postsContainer').append('<p class="text-red-500 text-center py-4">Error loading posts: ' + (response?.message || 'Unknown error') + '</p>');
            }
        });
    }

    function createPostElement(post) {
        const date = new Date(post.datePosted);
        const formattedDate = date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });

        const mediaContent = post.mediaBlobBase64 ?
            `<img src="${post.mediaBlobBase64}" alt="Post" class="w-full object-cover" />` :
            post.externalMediaUrl ?
                `<img src="${post.externalMediaUrl}" alt="Post" class="w-full object-cover" />` :
                `<div class="bg-gray-200 h-[400px] flex items-center justify-center">
                <span class="text-gray-500">No Media</span>
            </div>`;

        return `
            <div class="bg-white rounded-lg shadow-md max-w-xl mx-auto post-item" data-post-id="${post.id}">
                <!-- Post Header -->
                <div class="flex items-center p-4 border-b">
                    <div class="w-8 h-8 bg-gray-300 rounded-full mr-3"></div>
                    <div class="flex-1">
                        <p class="font-semibold">${post.username || 'User #' + post.authorId}</p>
                    </div>
                    <div class="flex items-center space-x-2">
                        <div class="text-gray-500">
                            <i class="fas fa-ellipsis-h"></i>
                        </div>
                    </div>
                </div>

                <!-- Post Media -->
                <div class="post-media relative cursor-pointer">
                    ${mediaContent}
                </div>

                <!-- Engagement Actions -->
                <div class="p-4">
                    <div class="flex space-x-4 mb-2">
                        <button class="like-button focus:outline-none" data-post-id="${post.id}">
                            <i class="far fa-heart text-2xl"></i>
                        </button>
                        <button class="comment-button focus:outline-none" data-post-id="${post.id}">
                            <i class="far fa-comment text-2xl"></i>
                        </button>
                        <div class="flex-grow"></div>
                    </div>

                    <!-- Like Count -->
                    <div class="mb-2">
                        <p class="font-semibold"><span class="likes-number">${post.likeCount}</span> likes</p>
                    </div>

                    <!-- Post Caption -->
                    <div class="mb-3">
                        <p>
                            <span class="font-semibold">${post.username || 'User #' + post.authorId}</span>
                            <span>${post.description}</span>
                        </p>
                    </div>

                    <!-- Post Metadata -->
                    <div class="text-gray-500 text-xs mb-3">
                        ${formattedDate}
                        ${post.creationYear ? ` · Created in ${post.creationYear}` : ''}
                        ${post.categoryId ? ` · Category ID: ${post.categoryId}` : ''}
                    </div>

                    <!-- Comments Indicator -->
                    <p class="text-gray-500 text-sm mb-2">
                        <span class="comment-count">${post.commentCount}</span> comments
                    </p>
                </div>
            </div>
        `;
    }

    loadPosts();

    $(document).on('click', '.post-media, .comment-button', function() {
        const postId = $(this).closest('.post-item').data('post-id');
        window.location.href = `${contextPath}/post.jsp?id=${postId}`;
    });
});