$(document).ready(function() {
    // DOM elements
    const postModal = $("#postModal");
    const newPostBtn = $("#newPostBtn");
    const postCloseBtn = $("#postModal .close-modal");
    const fileInput = $("#contentFile");
    const previewImage = $("#previewImage");
    const previewVideo = $("#previewVideo");
    const postForm = $("#postForm");
    const postMessageDiv = $("#postMessage");
    const postsContainer = $(".post-grid");
    const deleteProfileBtn = $("#deleteProfileBtn");
    const savedPostsBtn = $("#savedPostsBtn");
    const exportBtn = $("#exportBtn");
    const exportModal = $("#exportModal");
    const exportJsonBtn = $("#exportJsonBtn");
    const exportXmlBtn = $("#exportXmlBtn");
    const exportCloseBtn = exportModal.find(".close-modal");
    const importBtn = $("#importBtn");
    const importModal = $("#importModal");
    const importForm = $("#importForm");
    const importFileInput = $("#importFile");
    const importMessageDiv = $("#importMessage");
    const importCloseBtn = importModal.find(".close-modal");
    const namedTagSelect = $("#namedTagIds");
    const userTaggedSelect = $("#userTaggedIds");
    const username = $('.profile-info h1').text().trim();
    const initials = username.slice(0, 2).toUpperCase();

    // Initialize Select2
    namedTagSelect.select2({
        placeholder: "Select named tags",
        allowClear: true,
        width: '100%'
    });
    userTaggedSelect.select2({
        placeholder: "Select users to tag",
        allowClear: true,
        width: '100%'
    });

    async function loadNamedTags() {
        try {
            const response = await fetch('/wepProject_war_exploded/namedTags', {
                method: 'GET',
                headers: { 'Accept': 'application/json' }
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data?.message || 'Error loading named tags');
            }

            if (data.status === 'success' && data.data.namedTagMap) {
                namedTagSelect.empty();
                $.each(data.data.namedTagMap, function(id, name) {
                    namedTagSelect.append(`<option value="${id}">${name}</option>`);
                });
                namedTagSelect.trigger('change');
            }
        } catch (error) {
            console.error('Error loading named tags:', error);
            postMessageDiv.html('<p class="text-red-500">Failed to load tags</p>');
        }
    }

    async function loadUsers() {
        try {
            const response = await fetch('/wepProject_war_exploded/users', {
                method: 'GET',
                headers: { 'Accept': 'application/json' }
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data?.message || 'Error loading users');
            }

            if (data.status === 'success' && data.data.userMap) {
                userTaggedSelect.empty();
                $.each(data.data.userMap, function(id, username) {
                    userTaggedSelect.append(`<option value="${id}">${username}</option>`);
                });
                userTaggedSelect.trigger('change');
            }
        } catch (error) {
            console.error('Error loading users:', error);
            postMessageDiv.html('<p class="text-red-500">Failed to load users</p>');
        }
    }

    // Saved Posts Button
    if (savedPostsBtn.length) {
        console.log("Saved Posts button found, attaching event listener");
        savedPostsBtn.on("click", function(event) {
            event.preventDefault();
            window.location.href = "/wepProject_war_exploded/profile?saved=1";
        });
    } else {
        console.error("Saved Posts button not found in DOM");
    }

    // Import Modal Handling
    if (importBtn.length) {
        importBtn.on("click", function() {
            console.log("Opening import modal");
            importModal.css("display", "flex");
            importMessageDiv.empty();
            importForm[0].reset();
        });
    }

    if (importCloseBtn.length) {
        importCloseBtn.on("click", function() {
            console.log("Closing import modal");
            importModal.css("display", "none");
        });
    }

    if (importForm.length) {
        importForm.on("submit", async function(e) {
            e.preventDefault();
            const submitButton = importForm.find('button[type="submit"]');
            const originalButtonText = submitButton.text();
            submitButton.text("Importing...").prop("disabled", true);
            importMessageDiv.html('<p class="text-blue-500">Importing posts...</p>');

            const formData = new FormData(importForm[0]);
            const file = importFileInput[0].files[0];
            if (file) {
                console.log("Submitting import file:", file.name, file.size, file.type);
                if (!file.name.endsWith('.json') && !file.name.endsWith('.xml')) {
                    importMessageDiv.html('<p class="text-red-500">Please select a .json or .xml file</p>');
                    submitButton.text(originalButtonText).prop("disabled", false);
                    return;
                }
                if (file.size > 10 * 1024 * 1024) {
                    importMessageDiv.html('<p class="text-red-500">File is too large (max 10MB)</p>');
                    submitButton.text(originalButtonText).prop("disabled", false);
                    return;
                }
            } else {
                importMessageDiv.html('<p class="text-red-500">Please select a file</p>');
                submitButton.text(originalButtonText).prop("disabled", false);
                return;
            }

            try {
                const response = await fetch("/wepProject_war_exploded/import-saved-posts", {
                    method: "POST",
                    body: formData
                });

                const result = await response.json();
                console.log("Import response:", result);

                importMessageDiv.html(`<p class="${result.status === 'success' ? 'text-green-500' : 'text-red-500'}">${result.message}</p>`);

                if (result.status === "success") {
                    setTimeout(() => {
                        window.location.href = "/wepProject_war_exploded/profile?saved=1";
                    }, 1500);
                } else {
                    submitButton.text(originalButtonText).prop("disabled", false);
                }
            } catch (error) {
                console.error("Import error:", error);
                importMessageDiv.html(`<p class="text-red-500">Network error: ${error.message || 'Server error'}</p>`);
                submitButton.text(originalButtonText).prop("disabled", false);
            }
        });
    }

    // Export Modal Handling
    if (exportBtn.length) {
        exportBtn.on("click", function() {
            console.log("Opening export modal");
            exportModal.css("display", "flex");
        });
    }

    if (exportCloseBtn.length) {
        exportCloseBtn.on("click", function() {
            console.log("Closing export modal");
            exportModal.css("display", "none");
        });
    }

    // Close modals when clicking outside
    $(window).on("click", function(event) {
        if (event.target === exportModal[0]) {
            console.log("Closing export modal via background click");
            exportModal.css("display", "none");
        }
        if (event.target === importModal[0]) {
            console.log("Closing import modal via background click");
            importModal.css("display", "none");
        }
        if (event.target === postModal[0]) {
            console.log("Closing post modal via background click");
            closePostModal();
        }
    });

    if (exportJsonBtn.length) {
        exportJsonBtn.on("click", async function() {
            console.log("Exporting saved posts as JSON");
            try {
                const response = await fetch('/wepProject_war_exploded/export-saved-posts?format=json', {
                    method: 'GET'
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData?.message || 'Server error');
                }

                const data = await response.json();
                const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'saved_posts.json';
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
                exportModal.css("display", "none");
            } catch (error) {
                console.error("Error exporting JSON:", error);
                alert('Error exporting posts: ' + error.message);
                exportModal.css("display", "none");
            }
        });
    }

    if (exportXmlBtn.length) {
        exportXmlBtn.on("click", async function() {
            console.log("Exporting saved posts as XML");
            try {
                const response = await fetch('/wepProject_war_exploded/export-saved-posts?format=xml', {
                    method: 'GET'
                });

                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || 'Server error');
                }

                const xmlData = await response.text();
                const blob = new Blob([xmlData], { type: 'application/xml' });
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'saved_posts.xml';
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
                exportModal.css("display", "none");
            } catch (error) {
                console.error("Error exporting XML:", error);
                alert('Error exporting posts: ' + error.message);
                exportModal.css("display", "none");
            }
        });
    }

    // New Post Modal
    if (newPostBtn.length) {
        newPostBtn.on("click", function() {
            console.log("Opening new post modal");
            postModal.css("display", "flex");
            loadNamedTags();
            loadUsers();
        });
    }

    // Close post modal
    if (postCloseBtn.length) {
        postCloseBtn.on("click", function() {
            console.log("Closing post modal");
            closePostModal();
        });
    }

    if (deleteProfileBtn.length) {
        deleteProfileBtn.on("click", async function() {
            console.log('Delete button clicked');
            if (!confirm('Are you sure you want to delete your profile? This action cannot be undone.')) {
                return;
            }
            console.log('Confirmed deletion');

            try {
                const response = await fetch('/wepProject_war_exploded/profile', {
                    method: 'DELETE',
                    headers: { 'Accept': 'application/json' }
                });

                const data = await response.json();
                console.log('Response:', data);

                if (!response.ok) {
                    if (response.status === 401) {
                        alert('Please log in to delete your profile');
                        window.location.href = '/wepProject_war_exploded/login.jsp';
                        return;
                    }
                    throw new Error(data?.message || 'Error deleting profile');
                }

                if (data.status === 'success') {
                    alert('Profile deleted successfully.');
                    window.location.href = '/wepProject_war_exploded/login';
                } else {
                    alert(data.message || 'Error deleting profile');
                }
            } catch (error) {
                console.error('Delete profile error:', error);
                alert(error.message || 'Error deleting profile');
            }
        });
    } else {
        console.error('Delete Profile button not found');
    }

    // Close post modal and reset form
    function closePostModal() {
        postModal.css("display", "none");
        postForm[0].reset();
        previewImage.addClass("hidden").removeAttr("src");
        previewVideo.addClass("hidden").removeAttr("src");
        postMessageDiv.empty();
        namedTagSelect.val(null).trigger('change');
        userTaggedSelect.val(null).trigger('change');
    }

    // Image/Video Preview
    if (fileInput.length) {
        fileInput.on("change", function() {
            const file = fileInput[0].files[0];
            previewImage.addClass("hidden").removeAttr("src");
            previewVideo.addClass("hidden").removeAttr("src");
            postMessageDiv.empty();

            if (file) {
                console.log("File selected:", file.name, file.size, file.type);
                const isImage = file.type.startsWith('image/');
                const isVideo = file.type.startsWith('video/');
                const validVideoTypes = ['video/mp4', 'video/quicktime'];

                if (!isImage && !isVideo) {
                    postMessageDiv.html('<p class="text-red-500">Please select an image or video file</p>');
                    fileInput.val('');
                    return;
                }

                if (isVideo && !validVideoTypes.includes(file.type)) {
                    postMessageDiv.html('<p class="text-red-500">Only MP4 and MOV videos are supported</p>');
                    fileInput.val('');
                    return;
                }

                if (file.size > 50 * 1024 * 1024) {
                    postMessageDiv.html('<p class="text-red-500">File is too large (max 50MB)</p>');
                    fileInput.val('');
                    return;
                }

                const reader = new FileReader();
                reader.onload = function(e) {
                    if (isImage) {
                        previewImage.attr("src", e.target.result).removeClass("hidden");
                        console.log("Preview image loaded");
                    } else if (isVideo) {
                        previewVideo.attr("src", e.target.result).removeClass("hidden");
                        console.log("Preview video loaded");
                    }
                };
                reader.readAsDataURL(file);
            } else {
                console.log("No file selected");
            }
        });
    }

    if (postForm.length) {
        postForm.on("submit", async function(e) {
            e.preventDefault();
            const submitButton = postForm.find('button[type="submit"]');
            const originalButtonText = submitButton.text();
            submitButton.text("Uploading...").prop("disabled", true);
            postMessageDiv.html('<p class="text-blue-500">Uploading your post...</p>');

            const formData = new FormData(postForm[0]);
            // Append selected namedTagIds
            namedTagSelect.find("option:selected").each(function() {
                formData.append("namedTagIds[]", $(this).val());
            });
            // Append selected userTaggedIds
            userTaggedSelect.find("option:selected").each(function() {
                formData.append("userTaggedIds[]", $(this).val());
            });
            // Append media type
            const file = fileInput[0].files[0];
            if (file) {
                formData.append("mediaType", file.type.startsWith('image/') ? 'image' : 'video');
            }

            console.log("Submitting form with file:", formData.get("contentFile")?.name,
                "mediaType:", formData.get("mediaType"),
                "description:", formData.get("description"),
                "namedTagIds:", formData.getAll("namedTagIds[]"),
                "userTaggedIds:", formData.getAll("userTaggedIds[]"));

            try {
                const response = await fetch("/wepProject_war_exploded/import", {
                    method: "POST",
                    body: formData
                });

                const result = await response.json();
                console.log("Response JSON:", result);

                postMessageDiv.html(`<p class="${result.status === 'success' ? 'text-green-500' : 'text-red-500'}">${result.message}</p>`);

                if (result.status === "success") {
                    console.log("Post created successfully, reloading in 1.5s");
                    setTimeout(() => {
                        window.location.href = "/wepProject_war_exploded/profile";
                    }, 1500);
                } else {
                    submitButton.text(originalButtonText).prop("disabled", false);
                }
            } catch (error) {
                console.error("Network error details:", error);
                postMessageDiv.html(`<p class="text-red-500">Network error: ${error.message || 'Server error'}</p>`);
                submitButton.text(originalButtonText).prop("disabled", false);
            }
        });
    }

    async function loadPosts() {
        try {
            const response = await fetch("/wepProject_war_exploded/profile", {
                method: "GET",
                headers: { "X-Requested-With": "XMLHttpRequest" }
            });

            if (!response.ok) {
                throw new Error('Failed to load posts');
            }

            const html = await response.text();
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;
            const newPostsContainer = tempDiv.querySelector('.post-grid');
            if (newPostsContainer) {
                postsContainer.html(newPostsContainer.innerHTML);
            }
        } catch (error) {
            console.error("Error refreshing posts:", error);
        }
    }

});
