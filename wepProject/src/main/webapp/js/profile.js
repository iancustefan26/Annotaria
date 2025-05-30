document.addEventListener('DOMContentLoaded', function() {
    // DOM elements
    const postModal = document.getElementById("postModal");
    const newPostBtn = document.getElementById("newPostBtn");
    const postCloseBtn = document.querySelector(".close-modal");
    const fileInput = document.getElementById("contentFile");
    const previewImage = document.getElementById("previewImage");
    const postForm = document.getElementById("postForm");
    const postMessageDiv = document.getElementById("postMessage");
    const postsContainer = document.querySelector(".post-grid");
    const deleteProfileBtn = document.getElementById("deleteProfileBtn");
    const savedPostsBtn = document.getElementById("savedPostsBtn");
    const exportBtn = document.getElementById("exportBtn");
    const exportModal = document.getElementById("exportModal");
    const exportJsonBtn = document.getElementById("exportJsonBtn");
    const exportXmlBtn = document.getElementById("exportXmlBtn");
    const exportCloseBtn = exportModal?.querySelector(".close-modal");

    // Saved Posts Button
    if (savedPostsBtn) {
        console.log("Saved Posts button found, attaching event listener");
        savedPostsBtn.addEventListener("click", function(event) {
            event.preventDefault();
            window.location.href = "/wepProject_war_exploded/profile?saved=1";
        });
    } else {
        console.error("Saved Posts button not found in DOM");
    }

    // Export Modal Handling
    if (exportBtn) {
        exportBtn.onclick = () => {
            console.log("Opening export modal");
            exportModal.style.display = "flex";
        };
    }

    if (exportCloseBtn) {
        exportCloseBtn.onclick = () => {
            console.log("Closing export modal");
            exportModal.style.display = "none";
        };
    }

    // Close modals when clicking outside
    window.onclick = (event) => {
        if (event.target === exportModal) {
            console.log("Closing export modal via background click");
            exportModal.style.display = "none";
        }
        if (event.target === postModal) {
            console.log("Closing post modal via background click");
            closePostModal();
        }
    };

    // Export JSON
    if (exportJsonBtn) {
        exportJsonBtn.onclick = () => {
            console.log("Exporting saved posts as JSON");
            $.ajax({
                url: '/wepProject_war_exploded/export-saved-posts',
                type: 'GET',
                data: { format: 'json' },
                success: function(response) {
                    const blob = new Blob([JSON.stringify(response, null, 2)], { type: 'application/json' });
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = 'saved_posts.json';
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                    window.URL.revokeObjectURL(url);
                    exportModal.style.display = "none";
                },
                error: function(xhr) {
                    console.error("Error exporting JSON:", xhr);
                    alert('Error exporting posts: ' + (xhr.responseJSON?.message || 'Server error'));
                    exportModal.style.display = "none";
                }
            });
        };
    }

    // Export XML
    if (exportXmlBtn) {
        exportXmlBtn.onclick = () => {
            console.log("Exporting saved posts as XML");
            $.ajax({
                url: '/wepProject_war_exploded/export-saved-posts',
                type: 'GET',
                data: { format: 'xml' },
                dataType: 'text', // Force text response to avoid XML parsing
                success: function(response) {
                    const blob = new Blob([response], { type: 'application/xml' });
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = 'saved_posts.xml';
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                    window.URL.revokeObjectURL(url);
                    exportModal.style.display = "none";
                },
                error: function(xhr) {
                    console.error("Error exporting XML:", xhr);
                    alert('Error exporting posts: ' + (xhr.responseText || 'Server error'));
                    exportModal.style.display = "none";
                }
            });
        };
    }

    // New Post Modal
    if (newPostBtn) {
        newPostBtn.onclick = () => {
            console.log("Opening new post modal");
            postModal.style.display = "flex";
        };
    }

    // Close post modal
    if (postCloseBtn) {
        postCloseBtn.onclick = () => {
            console.log("Closing post modal");
            closePostModal();
        };
    }

    // Delete Profile
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
                headers: { 'Accept': 'application/json' },
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
                        window.location.href = '/wepProject_war_exploded/login.jsp';
                    } else {
                        alert(response?.message || 'Error deleting profile');
                    }
                }
            });
        };
    } else {
        console.error('Delete Profile button not found');
    }

    // Close post modal and reset form
    function closePostModal() {
        postModal.style.display = "none";
        postForm.reset();
        previewImage.style.display = "none";
        postMessageDiv.innerHTML = "";
    }

    // Image Preview
    if (fileInput) {
        fileInput.onchange = () => {
            const file = fileInput.files[0];
            if (file) {
                console.log("File selected:", file.name, file.size, file.type);
                if (!file.type.startsWith('image/')) {
                    postMessageDiv.innerHTML = '<p class="text-red-500">Please select an image file</p>';
                    fileInput.value = '';
                    return;
                }
                if (file.size > 10 * 1024 * 1024) {
                    postMessageDiv.innerHTML = '<p class="text-yellow-500">Warning: Large image files may take longer to upload</p>';
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

    // Form Submission
    if (postForm) {
        postForm.onsubmit = async (e) => {
            e.preventDefault();
            const submitButton = postForm.querySelector('button[type="submit"]');
            const originalButtonText = submitButton.innerText;
            submitButton.innerText = "Uploading...";
            submitButton.disabled = true;
            postMessageDiv.innerHTML = '<p class="text-blue-500">Uploading your post...</p>';

            const formData = new FormData(postForm);
            console.log("Submitting form with file:", formData.get("contentFile")?.name, "description:", formData.get("description"));

            try {
                const response = await fetch("/wepProject_war_exploded/import", {
                    method: "POST",
                    body: formData
                });
                console.log("Fetch response status:", response.status, response.statusText);
                const result = await response.json();
                console.log("Fetch response JSON:", result);
                postMessageDiv.innerHTML = `<p class="${result.status === 'success' ? 'text-green-500' : 'text-red-500'}">${result.message}</p>`;

                if (result.status === "success") {
                    console.log("Post created successfully, reloading in 1.5s");
                    setTimeout(() => {
                        window.location.href = "/wepProject_war_exploded/profile";
                    }, 1500);
                } else {
                    submitButton.innerText = originalButtonText;
                    submitButton.disabled = false;
                }
            } catch (error) {
                console.error("Network error details:", error.message, error.stack);
                postMessageDiv.innerHTML = `<p class="text-red-500">Network error: ${error.message}</p>`;
                submitButton.innerText = originalButtonText;
                submitButton.disabled = false;
            }
        };
    }

    // Load Posts
    function loadPosts() {
        fetch("/wepProject_war_exploded/profile", {
            method: "GET",
            headers: { "X-Requested-With": "XMLHttpRequest" }
        })
            .then(response => response.text())
            .then(html => {
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