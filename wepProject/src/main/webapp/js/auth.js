document.addEventListener("DOMContentLoaded", () => {
    const USERNAME_PATTERN = /^[a-zA-Z0-9_]{3,20}$/;
    const EMAIL_PATTERN = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    const CSRF_TOKEN_PATTERN = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

    function escapeHtml(unsafe) {
        if (!unsafe) return "";
        return unsafe
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    function validateInput(field, value, pattern, fieldName) {
        if (!value || !pattern.test(value)) {
            throw new Error(`Invalid ${fieldName} format`);
        }
        return escapeHtml(value);
    }

    function displayMessage(messageDiv, type, text) {
        messageDiv.className = `message ${type}-message`;
        messageDiv.innerHTML = ''; // Clear previous content
        const icon = document.createElement('i');
        icon.className = `fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}`;
        const span = document.createElement('span');
        span.textContent = text;
        messageDiv.appendChild(icon);
        messageDiv.appendChild(span);
        messageDiv.style.display = 'flex';
    }

    function handleFormSubmit(formId, url, redirectUrl, includeEmail = false) {
        const form = document.getElementById(formId);
        if (!form) return;

        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            const messageDiv = document.getElementById("message");

            try {
                let username, email, password, csrfToken;

                try {
                    username = validateInput(
                        "username",
                        document.getElementById("username").value,
                        USERNAME_PATTERN,
                        "username"
                    );
                    password = document.getElementById("password").value;
                    csrfToken = validateInput(
                        "csrfToken",
                        document.querySelector(`#${formId} [name='csrfToken']`).value,
                        CSRF_TOKEN_PATTERN,
                        "CSRF token"
                    );
                    if (includeEmail) {
                        email = validateInput(
                            "email",
                            document.getElementById("email").value,
                            EMAIL_PATTERN,
                            "email"
                        );
                    }
                } catch (validationError) {
                    console.error('Validation error:', validationError.message);
                    displayMessage(messageDiv, "error", validationError.message);
                    return;
                }

                const payload = includeEmail
                    ? { username, email, password, csrfToken }
                    : { username, password, csrfToken };

                const response = await fetch(url, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });

                console.log('Response status:', response.status);

                if (response.ok) {
                    const result = await response.json();
                    console.log('Response data:', result);

                    if (result.status === "success") {
                        displayMessage(messageDiv, "success", result.message);
                        setTimeout(() => {
                            window.location.href = redirectUrl;
                        }, 1000);
                    } else {
                        displayMessage(messageDiv, "error", result.message);
                    }
                } else {
                    try {
                        const errorResult = await response.json();
                        console.log('Error response:', errorResult);
                        displayMessage(messageDiv, "error", errorResult.message || 'Server error occurred');
                    } catch (jsonError) {
                        console.error('Failed to parse error response:', jsonError);
                        displayMessage(messageDiv, "error", `Server error (${response.status})`);
                    }
                }
            } catch (error) {
                console.error('Network error:', error);
                displayMessage(messageDiv, "error", "Network error, please try again");
            }
        });
    }

    if (document.getElementById("loginForm")) {
        handleFormSubmit("loginForm", "login", "feed");
    }
    if (document.getElementById("signupForm")) {
        handleFormSubmit("signupForm", "signup", "login.jsp", true);
    }
});

function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const icon = input.nextElementSibling?.querySelector('i');

    if (!input || !icon) return;

    if (input.type === 'password') {
        input.type = 'text';
        icon.className = 'fas fa-eye-slash';
    } else {
        input.type = 'password';
        icon.className = 'fas fa-eye';
    }
}
