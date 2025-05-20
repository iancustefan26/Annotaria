document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById("postModal");
    const imageModal = document.getElementById("imageModal");
    const newPostBtn = document.getElementById("newPostBtn");
    const postCloseBtn = document.querySelector(".close-modal");
    const imageCloseBtn = document.querySelector("#imageModal .close");
    const fileInput = document.getElementById("contentFile");
    const previewImage = document.getElementById("previewImage");
    const form = document.getElementById("postForm");
    const messageDiv = document.getElementById("postMessage");
    const postsContainer = document.getElementById("postsContainer");
    const enlargedImage = document.getElementById("enlargedImage");
    const imageDescription = document.getElementById("imageDescription");
    const likeButton = document.getElementById("likeButton");
    const commentButton = document.getElementById("commentButton");
    const likeCount = document.getElementById("likeCount");
    const commentCount = document.getElementById("commentCount");
    const commentInput = document.getElementById("commentInput");
    const submitComment = document.getElementById("submitComment");
    const commentsContainer = document.getElementById("commentsContainer");

    newPostBtn.onclick = () => {
        console.log("Opening new post modal");
        modal.style.display = "flex";
    };

    postCloseBtn.onclick = () => {
        console.log("Closing post modal");
        modal.style.display = "none";
        form.reset();
        previewImage.style.display = "none";
        messageDiv.innerHTML = "";
    };

    imageCloseBtn.onclick = () => {
        console.log("Closing image modal");
        imageModal.style.display = "none";
        commentInput.value = "";
        commentsContainer.innerHTML = "";
    };

    window.onclick = (event) => {
        if (event.target === modal) {
            console.log("Closing post modal via background click");
            modal.style.display = "none";
            form.reset();
            previewImage.style.display = "none";
            messageDiv.innerHTML = "";
        } else if (event.target === imageModal) {
            console.log("Closing image modal via background click");
            imageModal.style.display = "none";
            commentInput.value = "";
            commentsContainer.innerHTML = "";
        }
    };

    fileInput.onchange = () => {
        const file = fileInput.files[0];
        if (file) {
            console.log("File selected:", file.name, file.size, file.type);
            const reader = new FileReader();
            reader.onload = (e) => {
                previewImage.src = e.target.result;
                previewImage.style.display = "block";
                console.log("Preview image loaded");
            };
            reader.readAsDataURL(file);
        } else {
            console.log("No file selected");
        }
    };

    form.onsubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData(form);
        console.log("Submitting form with file:", formData.get("contentFile")?.name, "description:", formData.get("description"));

        try {
            const response = await fetch("/wepProject_war_exploded/import", {
                method: "POST",
                body: formData
            });
            console.log("Fetch response status:", response.status, response.statusText);
            console.log("Response headers:", [...response.headers.entries()]);

            const responseText = await response.text();
            console.log("Raw response text:", responseText);

            let result;
            try {
                result = JSON.parse(responseText);
            } catch (e) {
                console.error("JSON parse error:", e.message, "Response text:", responseText);
                throw new Error("Server did not return valid JSON");
            }

            console.log("Fetch response JSON:", result);
            messageDiv.innerHTML = `<p class="${result.status === 'success' ? 'text-green-500' : 'text-red-500'}">${result.message}</p>`;

            if (result.status === "success") {
                console.log("Post created successfully, reloading in 1s");
                setTimeout(() => {
                    modal.style.display = "none";
                    form.reset();
                    previewImage.style.display = "none";
                    messageDiv.innerHTML = "";
                    loadPosts();
                }, 1000);
            }
        } catch (error) {
            console.error("Network error details:", error.message, error.stack);
            messageDiv.innerHTML = `<p class="text-red-500">Network error: ${error.message}</p>`;
        }
    };

    likeButton.onclick = async () => {
        const postId = likeButton.dataset.postId;
        console.log("Like button clicked for post ID:", postId);

        try {
            const response = await fetch("/wepProject_war_exploded/like", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ postId })
            });
            const result = await response.json();
            console.log("Like response:", result);

            if (result.status === "success") {
                likeCount.textContent = `${result.data.likeCount} likes`;
                likeIcon.textContent = result.data.userHasLiked ? "❤️" : "🤍";
            } else {
                alert(`Error: ${result.message}`);
            }
        } catch (error) {
            console.error("Error liking post:", error);
            alert("Failed to process like. Please try again.");
        }
    };

    commentButton.onclick = async () => {
        const postId = commentButton.dataset.postId;
        console.log("Comment button clicked for post ID:", postId);

        try {
            const response = await fetch(`/wepProject_war_exploded/comment?postId=${postId}`, {
                method: "GET",
                headers: { "Accept": "application/json" }
            });
            const result = await response.json();
            console.log("Comments response:", result);

            if (result.status === "success") {
                commentsContainer.innerHTML = "";
                if (result.data.length === 0) {
                    commentsContainer.innerHTML = '<p class="text-gray-500 text-sm">No comments yet.</p>';
                } else {
                    result.data.forEach(comment => {
                        const commentDiv = document.createElement("div");
                        commentDiv.className = "comment";
                        commentDiv.innerHTML = `
                            <span class="comment-username">${comment.username}</span>
                            <span class="comment-content">${comment.content}</span>
                            <span class="comment-timestamp">${new Date(comment.datePosted).toLocaleString()}</span>
                        `;
                        commentsContainer.appendChild(commentDiv);
                    });
                }
            } else {
                alert(`Error: ${result.message}`);
            }
        } catch (error) {
            console.error("Error fetching comments:", error);
            alert("Failed to load comments. Please try again.");
        }
    };

    submitComment.onclick = async () => {
        const postId = commentButton.dataset.postId;
        const content = commentInput.value.trim();
        console.log("Submit comment for post ID:", postId, "Content:", content);

        if (!content) {
            alert("Comment cannot be empty.");
            return;
        }

        try {
            const response = await fetch("/wepProject_war_exploded/comment", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ postId, content })
            });
            const result = await response.json();
            console.log("Comment response:", result);

            if (result.status === "success") {
                commentCount.textContent = `${result.data.commentCount} comments`;
                commentInput.value = "";
                // Refresh comments
                commentButton.click();
            } else {
                alert(`Error: ${result.message}`);
            }
        } catch (error) {
            console.error("Error adding comment:", error);
            alert("Failed to add comment. Please try again.");
        }
    };

    // Optional: Trigger comment submission on Enter key
    commentInput.onkeypress = (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            submitComment.click();
        }
    };


});