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

    likeButton.onclick = () => {
        console.log("Like button clicked for post ID:", likeButton.dataset.postId);
        // Placeholder for like/unlike action (requires LikeServlet)
        alert("Like functionality not implemented. Requires LikeServlet.");
    };

    commentButton.onclick = () => {
        console.log("Comment button clicked for post ID:", commentButton.dataset.postId);
        // Placeholder for comment action (requires CommentServlet)
        alert("Comment functionality not implemented. Requires CommentServlet.");
    };

    loadPosts();

    async function loadPosts() {
        try {
            const response = await fetch('/wepProject_war_exploded/posts', {
                method: 'GET',
                headers: { 'Accept': 'application/json' }
            });
            console.log(`Fetch posts response status: ${response.status}`);
            const text = await response.text();
            console.log(`Raw posts response text: ${text}`);
            const data = JSON.parse(text);

            postsContainer.innerHTML = '';
            if (data.status === 'success' && data.data && data.data.length > 0) {
                data.data.forEach(post => {
                    if (post.mediaBlobBase64) {
                        const postDiv = document.createElement('div');
                        postDiv.className = 'post';
                        postDiv.innerHTML = `
                            <img src="${post.mediaBlobBase64}" alt="Post Image" class="post-image" data-post-id="${post.id}" data-description="${post.description || 'No description'}">
                        `;
                        postDiv.querySelector('.post-image').addEventListener('click', () => {
                            enlargedImage.src = post.mediaBlobBase64;
                            imageDescription.textContent = post.description || 'No description';
                            likeButton.dataset.postId = post.id;
                            commentButton.dataset.postId = post.id;
                            likeCount.textContent = `${post.likeCount || 0} likes`;
                            commentCount.textContent = `${post.commentCount || 0} comments`;
                            imageModal.style.display = "flex";
                            console.log("Opened image modal for post ID:", post.id);
                        });
                        postsContainer.appendChild(postDiv);
                    }
                });
            } else {
                postsContainer.innerHTML = '<p class="text-center text-gray-500">No posts found.</p>';
            }
        } catch (error) {
            console.error('Error loading posts:', error);
            postsContainer.innerHTML = '<p class="text-center text-gray-500">Error loading posts: ' + error.message + '</p>';
        }
    }
});