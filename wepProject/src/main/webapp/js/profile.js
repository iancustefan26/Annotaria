document.addEventListener('DOMContentLoaded', () => {
    // DOM Elements
    const modal = document.getElementById("postModal");
    const newPostBtn = document.getElementById("newPostBtn");
    const postCloseBtn = document.querySelector(".close-modal");
    const fileInput = document.getElementById("contentFile");
    const previewImage = document.getElementById("previewImage");
    const form = document.getElementById("postForm");
    const messageDiv = document.getElementById("postMessage");
    const postsContainer = document.querySelector(".post-grid");
    const deleteProfileBtn = document.getElementById("deleteProfileBtn");

    if (newPostBtn) {
        newPostBtn.onclick = () => {
            console.log("Opening new post modal");
            modal.style.display = "flex";
        };
    }

    // Close modal when X is clicked
    if (postCloseBtn) {
        postCloseBtn.onclick = () => {
            console.log("Closing post modal");
            closePostModal();
        };
    }

    // Close modal when clicking outside
    window.onclick = (event) => {
        if (event.target === modal) {
            console.log("Closing post modal via background click");
            closePostModal();
        }
    };

    if (deleteProfileBtn) {
        deleteProfileBtn.onclick = () => {
            console.log('Delete button clicked');
            if (!confirm('Are you sure you want to delete your profile? This action cannot be undone.')) {
                return;
            }
            console.log('Confirmed deletion');
            $.ajax({
                url: '/wepProject_war_exploded/profile',
                type: 'DELETE',
                headers: {
                    'Accept': 'application/json'
                },
                success: function(response) {
                    console.log('Success response:', response);
                    if (response.status === 'success') {
                        alert('Profile deleted successfully.');
                        window.location.href = '/wepProject_war_exploded/login';
                    } else {
                        alert(response.message || 'Error deleting profile');
                    }
                },
                error: function(xhr) {
                    console.error('Error response:', xhr);
                    const response = xhr.responseJSON;
                    if (xhr.status === 401) {
                        alert('Please log in to delete your profile');
                        window.location.href = '${pageContext.request.contextPath}/login.jsp';
                    } else {
                        alert(response?.message || 'Error deleting profile');
                    }
                }
            });
        };
    } else {
        console.error('Delete Profile button not found');
    }

    // Function to close modal and reset form
    function closePostModal() {
        modal.style.display = "none";
        form.reset();
        previewImage.style.display = "none";
        messageDiv.innerHTML = "";
    }

    // Show image preview when file is selected
    if (fileInput) {
        fileInput.onchange = () => {
            const file = fileInput.files[0];
            if (file) {
                console.log("File selected:", file.name, file.size, file.type);

                // Validate file is an image
                if (!file.type.startsWith('image/')) {
                    messageDiv.innerHTML = '<p class="text-red-500">Please select an image file</p>';
                    fileInput.value = '';
                    return;
                }

                // Size validation - alert if over 10MB
                if (file.size > 10 * 1024 * 1024) {
                    messageDiv.innerHTML = '<p class="text-yellow-500">Warning: Large image files may take longer to upload</p>';
                }

                const reader = new FileReader();
                reader.onload = (e) => {
                    previewImage.src = e.target.result;
                    previewImage.style.display = "block";
                    console.log("Preview image loaded");
                };
                reader.readAsDataURL(file);
            } else {
                console.log("No file selected");
                previewImage.style.display = "none";
            }
        };
    }

    // Form submission
    if (form) {
        form.onsubmit = async (e) => {
            e.preventDefault();

            // Display loading state
            const submitButton = form.querySelector('button[type="submit"]');
            const originalButtonText = submitButton.innerText;
            submitButton.innerText = "Uploading...";
            submitButton.disabled = true;
            messageDiv.innerHTML = '<p class="text-blue-500">Uploading your post...</p>';

            const formData = new FormData(form);
            console.log("Submitting form with file:", formData.get("contentFile")?.name, "description:", formData.get("description"));

            try {
                const response = await fetch("/wepProject_war_exploded/import", {
                    method: "POST",
                    body: formData
                });
                console.log("Fetch response status:", response.status, response.statusText);

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
                    console.log("Post created successfully, reloading in 1.5s");
                    setTimeout(() => {
                        // Redirect to profile page to see the new post
                        window.location.href = "/wepProject_war_exploded/profile";
                    }, 1500);
                } else {
                    // Reset button if there was an error
                    submitButton.innerText = originalButtonText;
                    submitButton.disabled = false;
                }
            } catch (error) {
                console.error("Network error details:", error.message, error.stack);
                messageDiv.innerHTML = `<p class="text-red-500">Network error: ${error.message}</p>`;
                submitButton.innerText = originalButtonText;
                submitButton.disabled = false;
            }
        };
    }

    function loadPosts() {
        fetch("/wepProject_war_exploded/profile", {
            method: "GET",
            headers: {
                "X-Requested-With": "XMLHttpRequest" // Signal this is an AJAX request
            }
        })
            .then(response => response.text())
            .then(html => {
                // This is a simple approach - in production you might want to use JSON responses
                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = html;
                const newPostsContainer = tempDiv.querySelector('.post-grid');
                if (newPostsContainer) {
                    postsContainer.innerHTML = newPostsContainer.innerHTML;
                }
            })
            .catch(error => {
                console.error("Error refreshing posts:", error);
            });
    }
});