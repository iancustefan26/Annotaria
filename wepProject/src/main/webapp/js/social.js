function updateLikeUI(postId, liked, count, container) {
    const postDiv = container.find(`[data-post-id="${postId}"]`);
    const likeIcon = postDiv.find('.likeButton i');
    if (liked) {
        likeIcon.removeClass('far').addClass('fas text-red-500');
    } else {
        likeIcon.removeClass('fas text-red-500').addClass('far');
    }
    postDiv.find('.likesNumber').text(count);
}

function toggleLike(postId, container) {
    const requestBody = {
        postId: postId.toString() // Ensure string
    };
    console.log('Toggling like:', requestBody);

    $.ajax({
        url: '/wepProject_war_exploded/like',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(requestBody),
        success: function(response) {
            console.log('Like response:', response);
            if (response.status === 'success') {
                const likeData = response.data;
                updateLikeUI(postId, likeData.userHasLiked, likeData.likeCount, container);
            }
        },
        error: function(xhr) {
            console.error('Like error:', xhr.responseJSON);
            const response = xhr.responseJSON;
            if (xhr.status === 401) {
                alert('Please log in to like posts');
                window.location.href = '/wepProject_war_exploded/login.jsp';
            } else {
                alert(response?.message || 'Error processing like');
            }
        }
    });
}

function updateSaveUI(postId, saved, container) {
    const postDiv = container.find(`[data-post-id="${postId}"]`);
    const saveIcon = postDiv.find('.saveButton i'); // Use class-based selector
    if (saved) {
        saveIcon.removeClass('far').addClass('fas');
    } else {
        saveIcon.removeClass('fas').addClass('far');
    }
}

function toggleSave(postId, container) {
    const postDiv = container.find(`[data-post-id="${postId}"]`);
    const saveIcon = postDiv.find('.saveButton i');
    const isSaved = saveIcon.hasClass('fas');
    const requestBody = {
        postId: postId.toString(),
        save: !isSaved
    };
    console.log('Toggling save:', requestBody);

    $.ajax({
        url: '/wepProject_war_exploded/save',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(requestBody),
        success: function(response) {
            console.log('Save response:', response);
            if (response.status === 'success') {
                updateSaveUI(postId, !isSaved, container);
                // Remove alert to prevent pop-ups
            } else {
                alert(response.message || 'Error processing save');
            }
        },
        error: function(xhr) {
            console.error('Save error:', xhr.responseJSON);
            const response = xhr.responseJSON;
            if (xhr.status === 401) {
                alert('Please log in to save posts');
                window.location.href = '/wepProject_war_exploded/login.jsp';
            } else {
                alert(response?.message || 'Error processing save');
            }
        }
    });
}

function submitComment(postId, commentInput, commentsContainer, commentCountElement, successCallback) {
    const content = commentInput.val().trim();
    if (!content) {
        console.log('Comment empty, skipping submission');
        return;
    }

    const requestBody = {
        postId: postId.toString(),
        content: content
    };
    console.log('Submitting comment:', requestBody);

    $.ajax({
        url: '/wepProject_war_exploded/comment',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(requestBody),
        success: function(response) {
            console.log('Comment response:', response);
            if (response.status === 'success') {
                const commentData = response.data;
                const commentHtml = createCommentElement(commentData);
                commentsContainer.removeClass('hidden').prepend(commentHtml);
                commentCountElement.text(commentData.commentCount);
                commentInput.val('');
                if (successCallback) successCallback();
            }
        },
        error: function(xhr) {
            console.error('Comment error:', xhr.responseJSON);
            const response = xhr.responseJSON;
            if (xhr.status === 401) {
                alert('Please log in to comment');
                window.location.href = '/wepProject_war_exploded/login.jsp';
            } else {
                alert(response?.message || 'Error posting comment');
            }
        }
    });
}

function loadComments(postId, commentsContainer, commentCountElement) {
    $.ajax({
        url: `/wepProject_war_exploded/comment?postId=${postId}`,
        type: 'GET',
        headers: { 'Accept': 'application/json' },
        success: function(response) {
            if (response.status === 'success' && Array.isArray(response.data)) {
                commentsContainer.empty();
                if (response.data.length > 0) {
                    response.data.forEach(comment => {
                        const commentHtml = createCommentElement(comment);
                        commentsContainer.append(commentHtml);
                    });
                } else {
                    commentsContainer.append('<p class="text-gray-500 text-center py-4">No comments yet</p>');
                }
                commentCountElement.text(response.data.length);
            }
        },
        error: function(xhr) {
            console.error('Failed to load comments for post ' + postId, xhr.responseJSON?.message);
        }
    });
}

function createCommentElement(comment) {
    const date = new Date(comment.datePosted);
    const formattedDate = date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });

    const isOwnComment = comment.isOwnComment;
    const deleteButton = isOwnComment ? `
    <button class="ml-2 text-gray-500 hover:text-red-500 delete-comment-btn" data-comment-id="${comment.id}" title="Delete Comment">
      <i class="fas fa-trash-alt"></i>
    </button>
  ` : '';

    return `
    <div class="mb-3 comment-item" data-comment-id="${comment.id}">
      <div class="flex">
        <p>
          <span class="font-semibold">${comment.username || 'User #' + comment.userId}</span>
          <span>${comment.content}</span>
        </p>
      </div>
      <div class="text-xs text-gray-500 mt-1">
        ${formattedDate}
        <button class="ml-2 text-gray-500 hover:text-gray-700 like-comment-btn">Like</button>
        <button class="ml-2 text-gray-500 hover:text-gray-700 reply-comment-btn">Reply</button>
        ${deleteButton}
      </div>
    </div>
  `;
}

function handleDoubleTap(postId, container, likeButton) {
    let lastTap = 0;
    container.find(`[data-post-id="${postId}"] .post-media`).on('click', function(e) {
        const currentTime = new Date().getTime();
        const tapLength = currentTime - lastTap;

        if (tapLength < 500 && tapLength > 0) {
            const likeIcon = likeButton.find('i');
            if (!likeIcon.hasClass('fas')) {
                likeButton.click();

                const heart = $('<i class="fas fa-heart text-white text-8xl absolute animate-ping"></i>');
                heart.css({
                    top: '50%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)'
                });

                $(this).append(heart);

                setTimeout(function() {
                    heart.remove();
                }, 1000);
            }
        }

        lastTap = currentTime;
    });
}

function setupCommentDeletion(container) {
    container.on('click', '.delete-comment-btn', function() {
        const commentId = $(this).data('comment-id');
        const postId = $(this).closest('.commentsContainer').data('post-id');
        if (!commentId) return;

        if (!confirm('Are you sure you want to delete this comment?')) {
            return;
        }

        $.ajax({
            url: `/wepProject_war_exploded/comment?id=${commentId}`,
            type: 'DELETE',
            headers: { 'Accept': 'application/json' },
            success: function(response) {
                if (response.status === 'success') {
                    $(`.comment-item[data-comment-id="${commentId}"]`).remove();
                    const postDiv = container.find(`[data-post-id="${postId}"]`);
                    const currentCount = parseInt(postDiv.find('.commentCount').text()) || 0;
                    postDiv.find('.commentCount').text(currentCount - 1);
                    if (postDiv.find('.commentsContainer').children().length === 0) {
                        postDiv.find('.commentsContainer').append('<p class="text-gray-500 text-center py-4">No comments yet</p>');
                    }
                    alert('Comment deleted successfully');
                }
            },
            error: function(xhr) {
                const response = xhr.responseJSON;
                if (xhr.status === 401) {
                    alert('Please log in to delete this comment');
                    window.location.href = '/wepProject_war_exploded/login.jsp';
                } else {
                    alert(response?.message || 'Error deleting comment');
                }
            }
        });
    });
}