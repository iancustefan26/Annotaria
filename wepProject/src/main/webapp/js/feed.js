$(document).ready(function() {
  let isLoading = false;
  let offset = 0;
  const limit = 5;
  let totalPosts = 0;
  let hasMorePosts = true;

  $('#tagFilter').select2({
    placeholder: "Select a tag",
    allowClear: true,
    width: '100%'
  });

  $('#userSearch').select2({
    placeholder: "",
    allowClear: true,
    width: '100%',
    minimumInputLength: 1,
    ajax: {
      url: '/users',
      dataType: 'json',
      delay: 250,
      data: function(params) {
        return { term: params.term || '' };
      },
      processResults: function(data) {
        if (data.status !== 'success' || !data.data.userMap) {
          console.error('Failed to fetch users:', data.message);
          return { results: [] };
        }
        const results = Object.entries(data.data.userMap).map(([id, username]) => ({
          id: id,
          text: username
        }));
        console.log('User search results:', results);
        return { results: results };
      },
      cache: true
    }
  })
      .on('select2:select', function(e) {
        const userId = e.params.data.id;
        console.log('Selected user:', userId);
        window.location.href = `/profile?userId=${userId}`;
      })
      .on('select2:clear', function() {
        console.log('Search cleared');
      });

  async function loadCategories() {
    try {
      const response = await fetch('/categories', {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
      });

      const data = await response.json();

      if (response.ok && data.status === 'success') {
        // Update both mobile and desktop category filters
        const categorySelects = $('#categoryFilter, #desktopCategoryFilter');
        categorySelects.empty();
        categorySelects.append('<option value="">All Categories</option>');
        for (const [id, name] of Object.entries(data.data.categoryMap)) {
          categorySelects.append(`<option value="${id}">${name}</option>`);
        }
      }
    } catch (error) {
      console.error('Failed to load categories:', error.message || 'Server error');
    }
  }

  async function loadYears() {
    const yearSelects = $('#yearFilter, #desktopYearFilter');
    yearSelects.empty();
    yearSelects.append('<option value="">All Years</option>');
    const currentYear = new Date().getFullYear();
    for (let year = currentYear; year >= 2000; year--) {
      yearSelects.append(`<option value="${year}">${year}</option>`);
    }
  }

  async function loadTags() {
    try {
      const response = await fetch('/namedTags', {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
      });

      const data = await response.json();

      if (response.ok && data.status === 'success') {
        const tagSelects = $('#tagFilter, #desktopTagFilter');
        tagSelects.empty();
        tagSelects.append('<option value="">All Tags</option>');
        for (const [id, name] of Object.entries(data.data.namedTagMap)) {
          tagSelects.append(`<option value="${id}">${name}</option>`);
        }
      }
    } catch (error) {
      console.error('Failed to load tags:', error.message || 'Server error');
    }
  }

  async function loadLeaderboard() {
    try {
      const response = await fetch('/statistics?format=csv', {
        method: 'GET'
      });

      if (!response.ok) {
        throw new Error('Failed to load leaderboard data');
      }

      const csvText = await response.text();
      const leaderboardData = parseCSVToLeaderboard(csvText);
      renderLeaderboard(leaderboardData);
    } catch (error) {
      console.error('Failed to load leaderboard:', error);
      $('#leaderboardContainer, #mobileLeaderboardContainer').html('<p class="text-red-500 text-xs">Failed to load leaderboard</p>');
    }
  }

  function parseCSVToLeaderboard(csvText) {
    const lines = csvText.trim().split('\n').filter(line => line.trim());
    if (lines.length <= 1) return [];

    const data = [];

    for (let i = 1; i < lines.length; i++) {
      const line = lines[i].trim();
      if (!line) continue;

      const parts = line.split(',').map(part => part.trim().replace(/"/g, ''));

      if (parts.length >= 2) {
        const title = parts[0];
        const username = parts[1];
        const score = parts.length >= 3 ? parseInt(parts[2]) || 0 : 0;

        if (title && username) {
          data.push({
            title: title,
            username: username,
            score: score,
            isLastEntry: i === lines.length - 1
          });
        }
      }
    }

    const regularEntries = data.slice(0, -1).sort((a, b) => b.score - a.score);
    const lastEntry = data.length > 0 ? data[data.length - 1] : null;

    const sortedData = [...regularEntries];
    if (lastEntry) {
      sortedData.push(lastEntry);
    }

    return sortedData.slice(0, 10);
  }

  function renderLeaderboard(data) {
    const containers = $('#leaderboardContainer, #mobileLeaderboardContainer');

    if (!data || data.length === 0) {
      containers.html('<p class="text-gray-500 text-xs">No leaderboard data available</p>');
      return;
    }

    let html = '<div class="space-y-1">';

    data.forEach((entry, index) => {
      const username = entry.username || `User ${index + 1}`;
      const score = entry.score || 0;
      const title = entry.title || 'Points';

      if (entry.isLastEntry) {
        html += `
        <div class="leaderboard-item flex items-center justify-between p-2 rounded-lg bg-yellow-50 border border-yellow-200 transition-colors">
          <div class="flex items-center space-x-2">
            <div class="rank-indicator w-6 text-center">👑</div>
            <div class="user-info">
              <p class="text-xs font-bold text-yellow-600 truncate" title="@${username}">@${username}</p>
              <p class="text-xs text-yellow-500">${title}</p>
            </div>
          </div>
          <div class="score text-xs text-yellow-600 font-bold">
            ${score > 0 ? score : ''}
          </div>
        </div>
      `;
      } else {
        const rankIcon = index < 3 ?
            ['🥇', '🥈', '🥉'][index] :
            `<span class="text-xs text-gray-500">#${index + 1}</span>`;

        html += `
        <div class="leaderboard-item flex items-center justify-between p-2 rounded-lg hover:bg-gray-50 transition-colors">
          <div class="flex items-center space-x-2">
            <div class="rank-indicator w-6 text-center">${rankIcon}</div>
            <div class="user-info">
              <p class="text-xs font-medium text-gray-800 truncate" title="@${username}">@${username}</p>
              <p class="text-xs text-gray-500">${title}</p>
            </div>
          </div>
          <div class="score text-xs text-gray-600 font-semibold">
            ${score > 0 ? score : ''}
          </div>
        </div>
      `;
      }
    });

    html += '</div>';
    containers.html(html);
  }

  async function loadPosts(append = false, reset = false) {
    if (isLoading || (!append && !hasMorePosts)) return;
    isLoading = true;

    const categoryId = $('#categoryFilter').val() || $('#desktopCategoryFilter').val();
    const creationYear = $('#yearFilter').val() || $('#desktopYearFilter').val();
    const namedTagId = $('#tagFilter').val() || $('#desktopTagFilter').val();
    let queryParams = [];
    if (categoryId) queryParams.push(`categoryId=${categoryId}`);
    if (creationYear) queryParams.push(`creationYear=${creationYear}`);
    if (namedTagId) queryParams.push(`namedTagId=${namedTagId}`);
    queryParams.push(`offset=${offset}`);
    queryParams.push(`limit=${limit}`);
    if (reset) queryParams.push(`reset=true`);
    const queryString = queryParams.length ? '?' + queryParams.join('&') : '';
    const url = '/feed' + queryString;

    console.log('Loading posts with URL:', url);

    try {
      if (append) {
        $('#postsContainer').append('<div id="loadingIndicator" class="text-center py-4">Loading more posts...</div>');
      }

      const response = await fetch(url, {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
      });

      const data = await response.json();
      console.log('Fetch response:', data);

      if (response.ok && data.status === 'success') {
        const postsContainer = $('#postsContainer');
        if (!append) postsContainer.empty();
        const posts = data.data?.posts || [];
        totalPosts = data.data?.totalPosts || 0;
        offset = data.data?.offset + posts.length;
        hasMorePosts = data.data?.hasMorePosts || false;
        const categoryMap = data.data?.categoryMap || {};
        console.log('Received posts:', posts.length, posts);
        console.log('Category map:', categoryMap);
        console.log('Pagination info:', { offset, totalPosts, hasMorePosts });

        if (posts.length === 0 && !append) {
          postsContainer.append('<p class="text-gray-500 text-center">No posts available.</p>');
          hasMorePosts = false;
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
          const username = post.authorUsername || `User #${post.authorId}`;
          const initials = username.slice(0, 2).toUpperCase();

          const postHtml = `
    <div class="bg-white rounded-lg shadow-md max-w-xl mx-auto mb-8" data-post-id="${post.id}">
        <div class="flex items-center p-4 border-b">
            <div class="w-8 h-8 rounded-full avatar-placeholder mr-3" data-initials="${initials}"></div>
            <div class="flex-1">
                <p class="font-semibold">${username}</p>
            </div>
            <div class="flex items-center space-x-2">
                ${post.isOwnPost ? `
                    <button class="deleteButton focus:outline-none text-red-500 hover:text-red-700" data-post-id="${post.id}" title="Delete Post">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                ` : ''}
                <div class="text-gray-500">
                    <i class="fas fa-ellipsis-h"></i>
                </div>
            </div>
        </div>
        <div class="post-media relative">
            ${post.mediaBlobBase64 ? `
                ${isVideo ? `
                    <video controls class="w-full object-cover max-h-[400px]">
                        <source src="${post.mediaBlobBase64}" type="video/mp4">
                        Your browser does not support the video tag.
                    </video>
                ` : `
                    <img src="${post.mediaBlobBase64}" alt="Post" class="w-full object-cover" />
                `}
            ` : post.externalMediaUrl ? `
                ${isVideo ? `
                    <video controls class="w-full object-cover max-h-[400px]">
                        <source src="${post.externalMediaUrl}" type="video/mp4">
                        Your browser does not support the video tag.
                    </video>
                ` : `
                    <img src="${post.externalMediaUrl}" alt="Post" class="w-full object-cover" />
                `}
            ` : `
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
                    <span class="font-semibold">${username}</span>
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
        });

        // Set up event handlers for the newly added posts
        setupEventHandlers();

      } else {
        $('#postsContainer').html(
            '<p class="text-red-500 text-center">' + (data.message || 'Failed to load posts') + '</p>'
        );
        hasMorePosts = false;
      }
    } catch (error) {
      console.error('Fetch error:', error);
      $('#postsContainer').html(
          '<p class="text-red-500 text-center">Failed to load posts: ' + error.message + '</p>'
      );
      hasMorePosts = false;
    } finally {
      $('#loadingIndicator').remove();
      isLoading = false;
    }
  }

  function setupEventHandlers() {
    const container = $(document); // Use document as the container for event delegation
    console.log('Setting up event handlers');

    // Remove existing handlers to prevent duplicates
    container.off('click', '.likeButton');
    container.off('click', '.saveButton');
    container.off('click', '.commentButton');
    container.off('click', '.submitComment');
    container.off('keypress', '.commentInput');
    container.off('input', '.commentInput');
    container.off('click', '.deleteButton');

    container.on('click', '.likeButton', function(e) {
      e.preventDefault();
      const postId = $(this).data('post-id');
      console.log('Like button clicked for post:', postId);
      if (postId) {
        toggleLike(postId, $('#postsContainer'));
      } else {
        console.error('No post-id found on like button');
      }
    });

    container.on('click', '.saveButton', function(e) {
      e.preventDefault();
      const postId = $(this).data('post-id');
      console.log('Save button clicked for post:', postId);
      if (postId) {
        toggleSave(postId, $('#postsContainer'));
      } else {
        console.error('No post-id found on save button');
      }
    });

    container.on('click', '.commentButton', function(e) {
      e.preventDefault();
      const postId = $(this).data('post-id');
      console.log('Comment button clicked for post:', postId);
      if (postId) {
        const commentsContainer = $(`[data-post-id="${postId}"] .commentsContainer`);
        commentsContainer.toggleClass('hidden');
        if (!commentsContainer.hasClass('hidden')) {
          loadComments(
              postId,
              commentsContainer,
              $(`[data-post-id="${postId}"] .commentCount`)
          );
          $(`textarea[data-post-id="${postId}"]`).focus();
        }
      } else {
        console.error('No post-id found on comment button');
      }
    });

    container.on('click', '.submitComment', function(e) {
      e.preventDefault();
      const postId = $(this).data('post-id');
      console.log('Submit comment clicked for post:', postId);
      if (postId) {
        const commentInput = $(`textarea[data-post-id="${postId}"]`);
        const commentsContainer = $(`[data-post-id="${postId}"] .commentsContainer`);
        const commentCountElement = $(`[data-post-id="${postId}"] .commentCount`);
        submitComment(postId, commentInput, commentsContainer, commentCountElement);
      } else {
        console.error('No post-id found on submit comment button');
      }
    });

    container.on('keypress', '.commentInput', function(e) {
      if (e.which === 13 && !e.shiftKey) {
        e.preventDefault();
        const postId = $(this).data('post-id');
        console.log('Enter pressed for comment on post:', postId);
        if (postId) {
          const commentInput = $(this);
          const commentsContainer = $(`[data-post-id="${postId}"] .commentsContainer`);
          const commentCountElement = $(`[data-post-id="${postId}"] .commentCount`);
          submitComment(postId, commentInput, commentsContainer, commentCountElement);
        }
      }
    });

    container.on('input', '.commentInput', function() {
      this.style.height = 'auto';
      this.style.height = (this.scrollHeight) + 'px';
    });

    container.on('click', '.deleteButton', async function(e) {
      e.preventDefault();
      const postId = $(this).data('post-id');
      if (postId && confirm('Are you sure you want to delete this post?')) {
        try {
          const response = await fetch(`/post?id=${postId}`, {
            method: 'DELETE',
            headers: { 'Accept': 'application/json' }
          });

          const data = await response.json();

          if (!response.ok) {
            if (response.status === 401) {
              alert('Please log in to delete this post');
              window.location.href = '/login.jsp';
              return;
            }
            throw new Error(data?.message || 'Error deleting post');
          }

          if (data.status === 'success') {
            alert('Post deleted successfully');
            offset = 0;
            hasMorePosts = true;
            loadPosts(false, true); // Reset on delete
          } else {
            alert(data.message || 'Failed to delete post');
          }
        } catch (error) {
          console.error('Delete post error:', error);
          alert(error.message || 'Error deleting post');
        }
      }
    });

    setupCommentDeletion(container);

    // Set up double tap for existing posts
    $('[data-post-id]').each(function() {
      const postId = $(this).data('post-id');
      const likeButton = $(this).find('.likeButton');
      handleDoubleTap(postId, $('#postsContainer'), likeButton);
    });

    enhanceTouchInteractions();
  }

  // Enhance touch interactions for mobile
  function enhanceTouchInteractions() {
    $('.likeButton, .commentButton, .saveButton, .deleteButton, .submitComment').on('touchstart', function(e) {
      $(this).addClass('touch-active');
    }).on('touchend', function(e) {
      $(this).removeClass('touch-active');
    });
  }

  // Sync filter changes between mobile and desktop
  function syncFilters() {
    $('#categoryFilter').on('change', function() {
      $('#desktopCategoryFilter').val($(this).val());
    });

    $('#desktopCategoryFilter').on('change', function() {
      $('#categoryFilter').val($(this).val());
    });

    $('#yearFilter').on('change', function() {
      $('#desktopYearFilter').val($(this).val());
    });

    $('#desktopYearFilter').on('change', function() {
      $('#yearFilter').val($(this).val());
    });

    $('#tagFilter').on('change', function() {
      $('#desktopTagFilter').val($(this).val());
    });

    $('#desktopTagFilter').on('change', function() {
      $('#tagFilter').val($(this).val());
    });
  }

  // == Infinite Scroll Handler ==
  $(window).on('scroll', debounce(function() {
    if (isLoading || !hasMorePosts) return;

    const scrollPosition = $(window).scrollTop() + $(window).height();
    const documentHeight = $(document).height();
    const triggerThreshold = $(window).width() <= 640 ? 100 : 200;

    if (scrollPosition >= documentHeight - triggerThreshold) {
      console.log('Near bottom, loading more posts...');
      loadPosts(true); // Append mode
    }
  }, 100));

  // == Debounce Utility ==
  function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
      const later = () => {
        clearTimeout(timeout);
        func(...args);
      };
      clearTimeout(timeout);
      timeout = setTimeout(later, wait);
    };
  }

  // == Export Statistics ==
  function triggerDownload(format) {
    console.log(`Triggering download for format: ${format}`);
    const url = `/statistics?format=${format}`;
    const a = document.createElement('a');
    a.href = url;
    a.download = `statistics_export.${format}`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }

  $('#exportStatisticsBtn').on('click', function(e) {
    e.preventDefault();
    $('#exportDropdown').toggleClass('hidden');
  });

  $('#exportCsv').on('click', function(e) {
    e.preventDefault();
    triggerDownload('csv');
    $('#exportDropdown').addClass('hidden');
  });

  $('#exportSvg').on('click', function(e) {
    e.preventDefault();
    triggerDownload('svg');
    $('#exportDropdown').addClass('hidden');
  });

  $(document).on('click', function(e) {
    if (!$(e.target).closest('#exportStatisticsBtn, #exportDropdown').length) {
      $('#exportDropdown').addClass('hidden');
    }
  });

  // Initialize everything
  loadCategories();
  loadYears();
  loadTags();
  loadLeaderboard();
  syncFilters();
  setupEventHandlers();

  if ($('#postsContainer').children().length === 0) {
    loadPosts();
  }

  const debouncedLoadPosts = debounce(function() {
    offset = 0;
    hasMorePosts = true;
    loadPosts(false, true); // Reset on filter change
  }, 300);

  $('#categoryFilter, #yearFilter, #tagFilter, #desktopCategoryFilter, #desktopYearFilter, #desktopTagFilter').on('change', function() {
    console.log('Filter changed:', {
      categoryId: $('#categoryFilter').val() || $('#desktopCategoryFilter').val(),
      creationYear: $('#yearFilter').val() || $('#desktopYearFilter').val(),
      namedTagId: $('#tagFilter').val() || $('#desktopTagFilter').val()
    });
    debouncedLoadPosts();
  });

  setInterval(loadLeaderboard, 5 * 60 * 1000);
});
