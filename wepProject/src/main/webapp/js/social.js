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

async function toggleLike(postId, container) {
    const requestBody = {
        postId: postId.toString()
    };

    try {
        const response = await fetch('/like', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody)
        });

        const data = await response.json();
        console.log('Like response:', data);

        if (!response.ok) {
            if (response.status === 401) {
                alert('Please log in to like posts');
                window.location.href = '/login.jsp';
                return;
            }
            throw new Error(data?.message || 'Error processing like');
        }

        if (data.status === 'success') {
            const likeData = data.data;
            updateLikeUI(postId, likeData.userHasLiked, likeData.likeCount, container);
        }
    } catch (error) {
        console.error('Like error:', error);
        alert(error.message || 'Error processing like');
    }
}

function updateSaveUI(postId, saved, container) {
    const postDiv = container.find(`[data-post-id="${postId}"]`);
    const saveIcon = postDiv.find('.saveButton i');
    if (saved) {
        saveIcon.removeClass('far').addClass('fas');
    } else {
        saveIcon.removeClass('fas').addClass('far');
    }
}

async function toggleSave(postId, container) {
    const postDiv = container.find(`[data-post-id="${postId}"]`);
    const saveIcon = postDiv.find('.saveButton i');
    const isSaved = saveIcon.hasClass('fas');
    const requestBody = {
        postId: postId.toString(),
        save: !isSaved
    };

    try {
        const response = await fetch('/save', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody)
        });

        const data = await response.json();
        console.log('Save response:', data);

        if (!response.ok) {
            if (response.status === 401) {
                alert('Please log in to save posts');
                window.location.href = '/login.jsp';
                return;
            }
            throw new Error(data?.message || 'Error processing save');
        }

        if (data.status === 'success') {
            updateSaveUI(postId, !isSaved, container);
        } else {
            alert(data.message || 'Error processing save');
        }
    } catch (error) {
        console.error('Save error:', error);
        alert(error.message || 'Error processing save');
    }
}

async function submitComment(postId, commentInput, commentsContainer, commentCountElement, successCallback) {
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

    try {
        const response = await fetch('/comment', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody)
        });

        const data = await response.json();
        console.log('Comment response:', data);

        if (!response.ok) {
            if (response.status === 401) {
                alert('Please log in to comment');
                window.location.href = '/login.jsp';
                return;
            }
            throw new Error(data?.message || 'Error posting comment');
        }

        if (data.status === 'success') {
            const commentData = data.data;
            const commentHtml = createCommentElement(commentData);
            commentsContainer.removeClass('hidden').prepend(commentHtml);
            commentCountElement.text(commentData.commentCount);
            commentInput.val('');
            if (successCallback) successCallback();
        }
    } catch (error) {
        console.error('Comment error:', error);
        alert(error.message || 'Error posting comment');
    }
}

async function loadComments(postId, commentsContainer, commentCountElement) {
    try {
        const response = await fetch(`/comment?postId=${postId}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        const data = await response.json();

        if (response.ok && data.status === 'success' && Array.isArray(data.data)) {
            commentsContainer.empty();
            if (data.data.length > 0) {
                data.data.forEach(comment => {
                    const commentHtml = createCommentElement(comment);
                    commentsContainer.append(commentHtml);
                });
            } else {
                commentsContainer.append('<p class="text-gray-500 text-center py-4">No comments yet</p>');
            }
            commentCountElement.text(data.data.length);
        }
    } catch (error) {
        console.error('Failed to load comments for post ' + postId, error.message);
    }
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
    container.on('click', '.delete-comment-btn', async function() {
        const commentId = $(this).data('comment-id');
        const postId = $(this).closest('.commentsContainer').data('post-id');
        if (!commentId) return;

        if (!confirm('Are you sure you want to delete this comment?')) {
            return;
        }

        try {
            const response = await fetch(`/comment?id=${commentId}`, {
                method: 'DELETE',
                headers: {
                    'Accept': 'application/json'
                }
            });

            const data = await response.json();

            if (!response.ok) {
                if (response.status === 401) {
                    alert('Please log in to delete this comment');
                    window.location.href = '/login.jsp';
                    return;
                }
                throw new Error(data?.message || 'Error deleting comment');
            }

            if (data.status === 'success') {
                $(`.comment-item[data-comment-id="${commentId}"]`).remove();
                const postDiv = container.find(`[data-post-id="${postId}"]`);
                const currentCount = parseInt(postDiv.find('.commentCount').text()) || 0;
                postDiv.find('.commentCount').text(currentCount - 1);
                if (postDiv.find('.commentsContainer').children().length === 0) {
                    postDiv.find('.commentsContainer').append('<p class="text-gray-500 text-center py-4">No comments yet</p>');
                }
                alert('Comment deleted successfully');
            }
        } catch (error) {
            console.error('Delete comment error:', error);
            alert(error.message || 'Error deleting comment');
        }
    });
}
