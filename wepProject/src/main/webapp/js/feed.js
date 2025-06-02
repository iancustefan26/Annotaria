$(document).ready(function() {
    // Initialize Select2 for tag filter
    $('#tagFilter').select2({
        placeholder: "Select a tag",
        allowClear: true,
        width: '100%'
    });

    // Populate category dropdown
    function loadCategories() {
        $.ajax({
            url: '/wepProject_war_exploded/categories',
            type: 'GET',
            headers: { 'Accept': 'application/json' },
            success: function(response) {
                if (response.status === 'success') {
                    const categorySelect = $('#categoryFilter');
                    categorySelect.empty();
                    categorySelect.append('<option value="">All Categories</option>');
                    for (const [id, name] of Object.entries(response.data.categoryMap)) {
                        categorySelect.append(`<option value="${id}">${name}</option>`);
                    }
                }
            },
            error: function(xhr) {
                console.error('Failed to load categories:', xhr.responseJSON?.message || 'Server error');
            }
        });
    }

    // Populate year dropdown (2000 to current year)
    function loadYears() {
        const yearSelect = $('#yearFilter');
        yearSelect.empty();
        yearSelect.append('<option value="">All Years</option>');
        const currentYear = new Date().getFullYear();
        for (let year = currentYear; year >= 2000; year--) {
            yearSelect.append(`<option value="${year}">${year}</option>`);
        }
    }

    // Populate tag dropdown
    function loadTags() {
        $.ajax({
            url: '/wepProject_war_exploded/namedTags',
            type: 'GET',
            headers: { 'Accept': 'application/json' },
            success: function(response) {
                if (response.status === 'success') {
                    const tagSelect = $('#tagFilter');
                    tagSelect.empty();
                    tagSelect.append('<option value="">All Tags</option>');
                    for (const [id, name] of Object.entries(response.data.namedTagMap)) {
                        tagSelect.append(`<option value="${id}">${name}</option>`);
                    }
                    tagSelect.trigger('change');
                }
            },
            error: function(xhr) {
                console.error('Failed to load tags:', xhr.responseJSON?.message || 'Server error');
            }
        });
    }

    function loadPosts() {
        const categoryId = $('#categoryFilter').val();
        const creationYear = $('#yearFilter').val();
        const namedTagId = $('#tagFilter').val();
        let queryParams = [];
        if (categoryId) queryParams.push(`categoryId=${categoryId}`);
        if (creationYear) queryParams.push(`creationYear=${creationYear}`);
        if (namedTagId) queryParams.push(`namedTagId=${namedTagId}`);
        const queryString = queryParams.length ? '?' + queryParams.join('&') : '';
        const url = '/wepProject_war_exploded/feed' + queryString;

        console.log('Loading posts with URL:', url);

        $.ajax({
            url: url,
            type: 'GET',
            headers: { 'Accept': 'application/json' },
            success: function(response) {
                console.log('AJAX response:', response);
                if (response.status === 'success') {
                    const postsContainer = $('#postsContainer');
                    postsContainer.empty();
                    const posts = response.data?.posts || [];
                    const categoryMap = response.data?.categoryMap || {};
                    console.log('Received posts:', posts.length, posts);
                    console.log('Category map:', categoryMap);

                    if (posts.length === 0) {
                        postsContainer.append('<p class="text-gray-500 text-center">No posts available.</p>');
                        return;
                    }

                    posts.forEach(post => {
                        console.log('Rendering post:', {
                            id: post.id,
                            mediaType: post.mediaType,
                            hasMediaBlob: !!post.mediaBlobBase64,
                            hasExternalUrl: !!post.externalMediaUrl
                        });
                        const categoryName = post.categoryId ? categoryMap[post.categoryId] || 'Unknown category' : null;
                        const isVideo = post.mediaType && post.mediaType === 'video';
                        const postHtml = `
              <div class="bg-white rounded-lg shadow-md max-w-xl mx-auto mb-8" data-post-id="${post.id}">
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
                  ${post.mediaBlobBase64 ? (
                            isVideo ? `
                      <a href="/wepProject_war_exploded/post?id=${post.id}">
                        <video controls class="w-full object-cover max-h-[400px]">
                          <source src="${post.mediaBlobBase64}" type="video/mp4">
                          Your browser does not support the video tag.
                        </video>
                      </a>
                    ` : `
                      <a href="/wepProject_war_exploded/post?id=${post.id}">
                        <img src="${post.mediaBlobBase64}" alt="Post" class="w-full object-cover" />
                      </a>
                    `
                        ) : post.externalMediaUrl ? (
                            isVideo ? `
                      <a href="/wepProject_war_exploded/post?id=${post.id}">
                        <video controls class="w-full object-cover max-h-[400px]">
                          <source src="${post.externalMediaUrl}" type="video/mp4">
                          Your browser does not support the video tag.
                        </video>
                      </a>
                    ` : `
                      <a href="/wepProject_war_exploded/post?id=${post.id}">
                        <img src="${post.externalMediaUrl}" alt="Post" class="w-full object-cover" />
                      </a>
                    `
                        ) : `
                    <div class="bg-gray-200 h-[400px] flex items-center justify-center">
                      <span class="text-gray-500">No Media</span>
                    </div>
                  `}
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
                      <span class="font-semibold">${post.authorUsername || 'User #' + post.authorId}</span>
                      <span>${post.description}</span>
                    </p>
                  </div>
                  <div class="text-gray-500 text-xs mb-3">
                    ${new Date(post.datePosted).toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' })}
                    ${post.creationYear ? ` · Created in ${post.creationYear}` : ''}
                    ${categoryName ? ` · Category: ${categoryName}` : ''}
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
            `;
                        postsContainer.append(postHtml);
                        loadComments(post.id, postsContainer.find(`[data-post-id="${post.id}"] .commentsContainer`), postsContainer.find(`[data-post-id="${post.id}"] .commentCount`));
                        handleDoubleTap(post.id, postsContainer, postsContainer.find(`.likeButton[data-post-id="${post.id}"]`));
                    });

                    // Like button handler
                    postsContainer.find('.likeButton').on('click', function() {
                        const postId = $(this).data('post-id');
                        console.log('Liking post:', postId);
                        toggleLike(postId, postsContainer);
                    });

                    // Save button handler
                    postsContainer.find('.saveButton').on('click', function() {
                        const postId = $(this).data('post-id');
                        console.log('Saving post:', postId);
                        toggleSave(postId, postsContainer);
                    });

                    // Comment button handler
                    postsContainer.find('.commentButton').on('click', function() {
                        const postId = $(this).data('post-id');
                        const commentsContainer = postsContainer.find(`[data-post-id="${postId}"] .commentsContainer`);
                        commentsContainer.toggleClass('hidden');
                        if (!commentsContainer.hasClass('hidden')) {
                            loadComments(postId, commentsContainer, postsContainer.find(`[data-post-id="${postId}"] .commentCount`));
                            postsContainer.find(`textarea[data-post-id="${postId}"]`).focus();
                        }
                    });

                    // Submit comment handler
                    postsContainer.find('.submitComment').on('click', function() {
                        const postId = $(this).data('post-id');
                        console.log('Submitting comment for postId:', postId);
                        const commentInput = postsContainer.find(`textarea[data-post-id="${postId}"]`);
                        const commentsContainer = postsContainer.find(`[data-post-id="${postId}"] .commentsContainer`);
                        const commentCountElement = postsContainer.find(`[data-post-id="${postId}"] .commentCount`);
                        submitComment(postId, commentInput, commentsContainer, commentCountElement);
                    });

                    // Submit comment with Enter key
                    postsContainer.find('.commentInput').on('keypress', function(e) {
                        if (e.which === 13 && !e.shiftKey) {
                            e.preventDefault();
                            const postId = $(this).data('post-id');
                            console.log('Submitting comment for postId (Enter):', postId);
                            const commentInput = $(this);
                            const commentsContainer = postsContainer.find(`[data-post-id="${postId}"] .commentsContainer`);
                            const commentCountElement = postsContainer.find(`[data-post-id="${postId}"] .commentCount`);
                            submitComment(postId, commentInput, commentsContainer, commentCountElement);
                        }
                    });

                    // Auto-resize textarea
                    postsContainer.find('.commentInput').on('input', function() {
                        this.style.height = 'auto';
                        this.style.height = (this.scrollHeight) + 'px';
                    });

                    // Delete post handler
                    postsContainer.find('.deleteButton').on('click', function() {
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
                                    const response = xhr.responseJSON;
                                    if (xhr.status === 401) {
                                        alert('Please log in to delete this post');
                                        window.location.href = '/wepProject_war_exploded/login.jsp';
                                    } else {
                                        alert(response?.message || 'Error deleting post');
                                    }
                                }
                            });
                        }
                    });

                    // Setup comment deletion
                    setupCommentDeletion(postsContainer);
                } else {
                    $('#postsContainer').html('<p class="text-red-500 text-center">' + (response.message || 'Failed to load posts') + '</p>');
                }
            },
            error: function(xhr) {
                console.error('AJAX error:', xhr);
                $('#postsContainer').html('<p class="text-red-500 text-center">Failed to load posts: ' + (xhr.responseJSON?.message || 'Server error') + '</p>');
            }
        });
    }

    // Export statistics handler
    function triggerDownload(format) {
        console.log(`Triggering download for format: ${format}`);
        const url = `/wepProject_war_exploded/statistics?format=${format}`;
        const a = document.createElement('a');
        a.href = url;
        a.download = `statistics_export.${format}`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    }

    // Toggle export dropdown
    $('#exportStatisticsBtn').on('click', function(e) {
        e.preventDefault();
        $('#exportDropdown').toggleClass('hidden');
    });

    // Export CSV
    $('#exportCsv').on('click', function(e) {
        e.preventDefault();
        triggerDownload('csv');
        $('#exportDropdown').addClass('hidden');
    });

    // Export SVG
    $('#exportSvg').on('click', function(e) {
        e.preventDefault();
        triggerDownload('svg');
        $('#exportDropdown').addClass('hidden');
    });

    // Close dropdown when clicking outside
    $(document).on('click', function(e) {
        if (!$(e.target).closest('#exportStatisticsBtn, #exportDropdown').length) {
            $('#exportDropdown').addClass('hidden');
        }
    });

    loadCategories();
    loadYears();
    loadTags();
    loadPosts();

    $('#categoryFilter, #yearFilter, #tagFilter').on('change', function() {
        console.log('Filter changed:', {
            categoryId: $('#categoryFilter').val(),
            creationYear: $('#yearFilter').val(),
            namedTagId: $('#tagFilter').val()
        });
        loadPosts();
    });
});