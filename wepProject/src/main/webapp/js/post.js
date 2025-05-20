$(document).ready(function() {
    const postId = new URLSearchParams(window.location.search).get('id');
    let userHasLiked = false;

    // Check if user has liked this post on page load
    checkUserLike();

    // Load comments on page load
    loadComments();

    // Handle comment button click
    $('#commentButton').click(function() {
        // Ensure comments are loaded and visible
        loadComments();
        // Scroll to comments section
        $('html, body').animate({
            scrollTop: $('#commentsContainer').offset().top - 100
        }, 500);
        // Focus on comment input
        $('#commentInput').focus();
    });

    $('#likeButton').click(function() {
        if (!postId) return;

        $.ajax({
            url: '/wepProject_war_exploded/like',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ postId: postId }),
            success: function(response) {
                if (response.status === 'success') {
                    const likeData = response.data;
                    updateLikeUI(likeData.userHasLiked, likeData.likeCount);
                }
            },
            error: function(xhr) {
                const response = xhr.responseJSON;
                if (xhr.status === 401) {
                    alert('Please log in to like posts');
                    window.location.href = '/wepProject_war_exploded/login.jsp';
                } else {
                    alert(response?.message || 'Error processing like');
                }
            }
        });
    });

    // Handle comment submission
    $('#submitComment').click(function() {
        submitComment();
    });

    // Allow submitting comments with Enter key
    $('#commentInput').keypress(function(e) {
        if (e.which === 13 && !e.shiftKey) {
            e.preventDefault();
            submitComment();
        }
    });

    // Function to submit a comment
    function submitComment() {
        const content = $('#commentInput').val().trim();
        if (!content || !postId) return;

        $.ajax({
            url: '/wepProject_war_exploded/comment',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                postId: postId,
                content: content
            }),
            success: function(response) {
                if (response.status === 'success') {
                    const commentData = response.data;

                    // Add the new comment to the top of the list
                    const commentHtml = createCommentElement(commentData);
                    $('#commentsContainer').prepend(commentHtml);

                    // Update comment count
                    $('#commentCount').text(commentData.commentCount);

                    // Clear the input
                    $('#commentInput').val('');

                    // Make sure comments are visible
                    $('#commentsContainer').removeClass('hidden');
                }
            },
            error: function(xhr) {
                const response = xhr.responseJSON;
                if (xhr.status === 401) {
                    alert('Please log in to comment');
                    window.location.href = '/wepProject_war_exploded/login';
                } else {
                    alert(response?.message || 'Error posting comment');
                }
            }
        });
    }

    // Function to check if the user has liked the post
    function checkUserLike() {
        if (!postId) return;

        userHasLiked = $('#likeIcon').hasClass('fas');
        updateLikeUI(userHasLiked, $('#likesNumber').text());
    }

    function updateLikeUI(liked, count) {
        userHasLiked = liked;

        if (liked) {
            $('#likeIcon').removeClass('far').addClass('fas text-red-500');
        } else {
            $('#likeIcon').removeClass('fas text-red-500').addClass('far');
        }

        $('#likesNumber').text(count);
    }

    function loadComments() {
        if (!postId) return;

        $.ajax({
            url: `/wepProject_war_exploded/comment?postId=${postId}`,
            type: 'GET',
            success: function(response) {
                if (response.status === 'success' && Array.isArray(response.data)) {
                    const comments = response.data;

                    // Clear existing comments
                    $('#commentsContainer').empty();

                    // Add comments to container
                    if (comments.length > 0) {
                        comments.forEach(comment => {
                            const commentHtml = createCommentElement(comment);
                            $('#commentsContainer').append(commentHtml);
                        });
                    } else {
                        $('#commentsContainer').append('<p class="text-gray-500 text-center py-4">No comments yet</p>');
                    }

                    // Update comment count
                    $('#commentCount').text(comments.length);
                }
            },
            error: function(xhr) {
                const response = xhr.responseJSON;
                if (xhr.status === 401) {
                    console.log('User not logged in');
                } else {
                    console.error(response?.message || 'Error loading comments');
                    $('#commentsContainer').append('<p class="text-red-500 text-center py-4">Error loading comments</p>');
                }
            }
        });
    }

    // Function to create a comment element
    function createCommentElement(comment) {
        const date = new Date(comment.datePosted);
        const formattedDate = date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });

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
                </div>
            </div>
        `;
    }

    // Dynamically adjust textarea height based on content
    $('#commentInput').on('input', function() {
        this.style.height = 'auto';
        this.style.height = (this.scrollHeight) + 'px';
    });

    // Double-tap/click to like (for mobile and desktop)
    let lastTap = 0;
    $('.post-media').on('click', function(e) {
        const currentTime = new Date().getTime();
        const tapLength = currentTime - lastTap;

        if (tapLength < 300 && tapLength > 0) {
            // Double tap detected
            if (!userHasLiked) {
                $('#likeButton').click();

                // Show heart animation
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
});