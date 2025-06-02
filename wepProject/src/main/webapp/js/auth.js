function handleFormSubmit(formId, url, redirectUrl, includeEmail = false) {
    document.getElementById(formId).addEventListener("submit", async (e) => {
        e.preventDefault();
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;
        const messageDiv = document.getElementById("message");

        const payload = includeEmail
            ? { username, email: document.getElementById("email").value, password }
            : { username, password };

        try {
            const response = await fetch(url, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            console.log('Response status:', response.status); // Debug log

            // Check if response is OK (200-299)
            if (response.ok) {
                const result = await response.json();
                console.log('Response data:', result); // Debug log

                if (result.status === "success") {
                    messageDiv.className = "message success-message";
                    messageDiv.innerHTML = `<i class="fas fa-check-circle"></i><span>${result.message}</span>`;
                    messageDiv.style.display = 'flex';
                    window.location.href = redirectUrl;
                } else {
                    messageDiv.className = "message error-message";
                    messageDiv.innerHTML = `<i class="fas fa-exclamation-circle"></i><span>${result.message}</span>`;
                    messageDiv.style.display = 'flex';
                }
            } else {
                // Handle HTTP error status codes (400, 401, 500, etc.)
                try {
                    const errorResult = await response.json();
                    console.log('Error response:', errorResult); // Debug log
                    messageDiv.className = "message error-message";
                    messageDiv.innerHTML = `<i class="fas fa-exclamation-circle"></i><span>${errorResult.message || 'Server error occurred'}</span>`;
                    messageDiv.style.display = 'flex';
                } catch (jsonError) {
                    console.error('Failed to parse error response:', jsonError);
                    messageDiv.className = "message error-message";
                    messageDiv.innerHTML = `<i class="fas fa-exclamation-circle"></i><span>Server error (${response.status})</span>`;
                    messageDiv.style.display = 'flex';
                }
                messageDiv.style.display = 'block';
            }
        } catch (error) {
            console.error('Network error:', error); // Debug log
            messageDiv.className = "message error-message";
            messageDiv.innerHTML = `<i class="fas fa-exclamation-circle"></i><span>Network error, please try again</span>`;
            messageDiv.style.display = 'flex';
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    if (document.getElementById("loginForm")) {
        handleFormSubmit("loginForm", "login", "feed");
    }
    if (document.getElementById("signupForm")) {
        handleFormSubmit("signupForm", "signup", "login.jsp", true);
    }
});

function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const icon = input.nextElementSibling.querySelector('i');

    if (input.type === 'password') {
        input.type = 'text';
        icon.className = 'fas fa-eye-slash';
    } else {
        input.type = 'password';
        icon.className = 'fas fa-eye';
    }
}