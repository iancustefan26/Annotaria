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
            const result = await response.json();

            if (result.status === "success") {
                messageDiv.innerHTML = `<p class="text-green">${result.message}</p>`;
                window.location.href = redirectUrl;
            } else {
                messageDiv.innerHTML = `<p class="text-red">${result.message}</p>`;
            }
        } catch (error) {
            messageDiv.innerHTML = `<p class="text-red">Network error, please try again</p>`;
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