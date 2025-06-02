$(document).ready(function() {
    const postId = new URLSearchParams(window.location.search).get('id');
    const postsContainer = $('#postsContainer');
    const username = $('.username').text().trim();
    const initials = username.slice(0, 2).toUpperCase();

    if (!postId) {
        postsContainer.html('<p class="text-red-500 text-center">Invalid post ID</p>');
        return;
    }

    $('#commentButton').on('click', function() {
        loadComments(postId, postsContainer.find('.commentsContainer'), postsContainer.find('.commentCount'));
        $('html, body').animate({
            scrollTop: $('.commentsContainer').offset().top - 100
        }, 500);
        $('#commentInput').focus();
    });

    $('#likeButton').on('click', function() {
        console.log('Liking post:', postId);
        toggleLike(postId, postsContainer);
    });

    $('#saveButton').on('click', function() {
        console.log('Saving post:', postId);
        toggleSave(postId, postsContainer);
    });

    $('#submitComment').on('click', function() {
        console.log('Submitting comment for postId:', postId);
        const commentInput = $('#commentInput');
        const commentsContainer = postsContainer.find('.commentsContainer');
        const commentCountElement = postsContainer.find('.commentCount');
        submitComment(postId, commentInput, commentsContainer, commentCountElement, () => {
            commentsContainer.removeClass('hidden');
        });
    });

    $('#commentInput').on('keypress', function(e) {
        if (e.which === 13 && !e.shiftKey) {
            e.preventDefault();
            console.log('Submitting comment for postId (Enter):', postId);
            const commentInput = $(this);
            const commentsContainer = postsContainer.find('.commentsContainer');
            const commentCountElement = postsContainer.find('.commentCount');
            submitComment(postId, commentInput, commentsContainer, commentCountElement, () => {
                commentsContainer.removeClass('hidden');
            });
        }
    });

    $('#commentInput').on('input', function() {
        this.style.height = 'auto';
        this.style.height = (this.scrollHeight) + 'px';
    });

    $('#deleteButton').on('click', function() {
        if (!confirm('Are you sure you want to delete this post?')) {
            return;
        }

        $.ajax({
            url: `/wepProject_war_exploded/post?id=${postId}`,
            type: 'DELETE',
            headers: { 'Accept': 'application/json' },
            success: function(response) {
                if (response.status === 'success') {
                    alert('Post deleted successfully');
                    window.location.href = '/wepProject_war_exploded/profile';
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
    });

    $('.avatar-placeholder').attr('data-initials', initials);
    setupCommentDeletion(postsContainer);

    handleDoubleTap(postId, postsContainer, $('#likeButton'));

    loadComments(postId, postsContainer.find('.commentsContainer'), postsContainer.find('.commentCount'));
});