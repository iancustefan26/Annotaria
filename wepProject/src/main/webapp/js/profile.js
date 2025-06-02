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


    1

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

    // Load Named Tags
    function loadNamedTags() {
        $.ajax({
            url: '/wepProject_war_exploded/namedTags',
            type: 'GET',
            headers: { 'Accept': 'application/json' },
            success: function(response) {
                if (response.status === 'success' && response.data.namedTagMap) {
                    namedTagSelect.empty();
                    $.each(response.data.namedTagMap, function(id, name) {
                        namedTagSelect.append(`<option value="${id}">${name}</option>`);
                    });
                    namedTagSelect.trigger('change');
                }
            },
            error: function(xhr) {
                console.error('Error loading named tags:', xhr);
                postMessageDiv.html('<p class="text-red-500">Failed to load tags</p>');
            }
        });
    }

    // Load Users
    function loadUsers() {
        $.ajax({
            url: '/wepProject_war_exploded/users',
            type: 'GET',
            headers: { 'Accept': 'application/json' },
            success: function(response) {
                if (response.status === 'success' && response.data.userMap) {
                    userTaggedSelect.empty();
                    $.each(response.data.userMap, function(id, username) {
                        userTaggedSelect.append(`<option value="${id}">${username}</option>`);
                    });
                    userTaggedSelect.trigger('change');
                }
            },
            error: function(xhr) {
                console.error('Error loading users:', xhr);
                postMessageDiv.html('<p class="text-red-500">Failed to load users</p>');
            }
        });
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

    // Import Form Submission
    if (importForm.length) {
        importForm.on("submit", function(e) {
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

            $.ajax({
                url: "/wepProject_war_exploded/import-saved-posts",
                type: "POST",
                data: formData,
                processData: false,
                contentType: false,
                success: function(result) {
                    console.log("Import response:", result);
                    importMessageDiv.html(`<p class="${result.status === 'success' ? 'text-green-500' : 'text-red-500'}">${result.message}</p>`);
                    if (result.status === "success") {
                        setTimeout(() => {
                            window.location.href = "/wepProject_war_exploded/profile?saved=1";
                        }, 1500);
                    } else {
                        submitButton.text(originalButtonText).prop("disabled", false);
                    }
                },
                error: function(xhr) {
                    console.error("Import error:", xhr);
                    importMessageDiv.html(`<p class="text-red-500">Network error: ${xhr.responseJSON?.message || 'Server error'}</p>`);
                    submitButton.text(originalButtonText).prop("disabled", false);
                }
            });
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

    // Export JSON
    if (exportJsonBtn.length) {
        exportJsonBtn.on("click", function() {
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
                    exportModal.css("display", "none");
                },
                error: function(xhr) {
                    console.error("Error exporting JSON:", xhr);
                    alert('Error exporting posts: ' + (xhr.responseJSON?.message || 'Server error'));
                    exportModal.css("display", "none");
                }
            });
        });
    }

    // Export XML
    if (exportXmlBtn.length) {
        exportXmlBtn.on("click", function() {
            console.log("Exporting saved posts as XML");
            $.ajax({
                url: '/wepProject_war_exploded/export-saved-posts',
                type: 'GET',
                data: { format: 'xml' },
                dataType: 'text',
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
                    exportModal.css("display", "none");
                },
                error: function(xhr) {
                    console.error("Error exporting XML:", xhr);
                    alert('Error exporting posts: ' + (xhr.responseText || 'Server error'));
                    exportModal.css("display", "none");
                }
            });
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

    // Delete Profile
    if (deleteProfileBtn.length) {
        deleteProfileBtn.on("click", function() {
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

    // Form Submission
    if (postForm.length) {
        postForm.on("submit", function(e) {
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

            $.ajax({
                url: "/wepProject_war_exploded/import",
                type: "POST",
                data: formData,
                processData: false,
                contentType: false,
                success: function(result) {
                    console.log("Fetch response JSON:", result);
                    postMessageDiv.html(`<p class="${result.status === 'success' ? 'text-green-500' : 'text-red-500'}">${result.message}</p>`);
                    if (result.status === "success") {
                        console.log("Post created successfully, reloading in 1.5s");
                        setTimeout(() => {
                            window.location.href = "/wepProject_war_exploded/profile";
                        }, 1500);
                    } else {
                        submitButton.text(originalButtonText).prop("disabled", false);
                    }
                },
                error: function(xhr) {
                    console.error("Network error details:", xhr);
                    postMessageDiv.html(`<p class="text-red-500">Network error: ${xhr.responseJSON?.message || 'Server error'}</p>`);
                    submitButton.text(originalButtonText).prop("disabled", false);
                }
            });
        });
    }

    // Load Posts
    function loadPosts() {
        $.ajax({
            url: "/wepProject_war_exploded/profile",
            type: "GET",
            headers: { "X-Requested-With": "XMLHttpRequest" },
            success: function(html) {
                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = html;
                const newPostsContainer = tempDiv.querySelector('.post-grid');
                if (newPostsContainer) {
                    postsContainer.html(newPostsContainer.innerHTML);
                }
            },
            error: function(xhr) {
                console.error("Error refreshing posts:", xhr);
            }
        });
    }


    $('.avatar-placeholder').attr('data-initials', initials);
    $('#namedTagIds, #userTaggedIds').select2({
        placeholder: 'Select tags/users',
        width: '100%'
    });
});