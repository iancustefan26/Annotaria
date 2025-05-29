$(document).ready(function() {
    const postId = new URLSearchParams(window.location.search).get('id');
    const postsContainer = $('#postsContainer');

    if (!postId) {
        $('#postsContainer').html('<p class="text-red-500 text-center">Invalid post ID</p>');
        return;
    }

    $('#commentButton').click(function() {
        loadComments(postId, postsContainer.find('.commentsContainer'), postsContainer.find('.commentCount'));
        $('html, body').animate({
            scrollTop: $('.commentsContainer').offset().top - 100
        }, 500);
        $('#commentInput').focus();
    });

    postsContainer.find('.likeButton').on('click', function() {
        console.log('Liking post:', postId);
        toggleLike(postId, postsContainer);
    });

    postsContainer.find('.submitComment').on('click', function() {
        console.log('Submitting comment for postId:', postId);
        const commentInput = postsContainer.find('.commentInput');
        const commentsContainer = postsContainer.find('.commentsContainer');
        const commentCountElement = postsContainer.find('.commentCount');
        submitComment(postId, commentInput, commentsContainer, commentCountElement, () => {
            commentsContainer.removeClass('hidden');
        });
    });

    postsContainer.find('.commentInput').on('keypress', function(e) {
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

    postsContainer.find('.commentInput').on('input', function() {
        this.style.height = 'auto';
        this.style.height = (this.scrollHeight) + 'px';
    });

    $('#deleteButton').click(function() {
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

    setupCommentDeletion(postsContainer);

    handleDoubleTap(postId, postsContainer, postsContainer.find('.likeButton'));

    loadComments(postId, postsContainer.find('.commentsContainer'), postsContainer.find('.commentCount'));
});