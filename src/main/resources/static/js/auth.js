document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");
    const registerForm = document.getElementById("registerForm");
    const loginErrorElement = document.getElementById("loginError");
    const registerErrorElement = document.getElementById("registerError");

    const tabButtons = document.querySelectorAll(".tab-button");
    const tabContents = document.querySelectorAll(".tab-content");


    tabButtons.forEach(button => {
        button.addEventListener("click", () => {
            const targetTab = button.dataset.tab;

            tabButtons.forEach(btn => btn.classList.remove("active"));
            button.classList.add("active");

            tabContents.forEach(content => {
                content.classList.remove("active");
                if (content.id === targetTab) {
                    content.classList.add("active");
                }
            });
        });
    });

    registerForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        registerErrorElement.innerText = "";
        registerErrorElement.style.display = "none";

        const username = document.getElementById("registerUsername").value.trim();
        const password = document.getElementById("registerPassword").value.trim();
        const role = document.getElementById("registerRole").value;

        if (!username || !password) {
            registerErrorElement.innerText = "⚠️ Username and password cannot be empty.";
            registerErrorElement.style.display = "block";
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/auth/sign-up", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, password, role }),
            });

            if (!response.ok) {
                const errorData = await response.json();
                let errorMessage = "❌ Registration failed. Please try again.";

                if (response.status === 400 && errorData.message.includes("User already exists")) {
                    errorMessage = "⚠️ This username is already taken. Please choose another one.";
                }

                registerErrorElement.innerText = errorMessage;
                registerErrorElement.style.display = "block";
                return;
            }

            alert("✅ Registration successful! You can now log in.");
            window.location.href = "index.html";

        } catch (error) {
            console.error("🚨 Register Error:", error);
            registerErrorElement.innerText = "⚠️ Something went wrong. Please try again.";
            registerErrorElement.style.display = "block";
        }
    });


    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        loginErrorElement.innerText = "";
        loginErrorElement.style.display = "none";

        const username = document.getElementById("loginUsername").value.trim();
        const password = document.getElementById("loginPassword").value.trim();

        if (!username || !password) {
            loginErrorElement.innerText = "⚠️ Username and password cannot be empty.";
            loginErrorElement.style.display = "block";
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/auth/sign-in", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({ username, password }),
            });

            if (!response.ok) {
                const errorData = await response.json();
                let errorMessage = "❌ Invalid username or password.";

                if (response.status === 404 && errorData.message.includes("User not found")) {
                    errorMessage = "⚠️ No account found with this username.";
                } else if (response.status === 401 && errorData.message.includes("Invalid password")) {
                    errorMessage = "⚠️ Incorrect password. Please try again.";
                }

                loginErrorElement.innerText = errorMessage;
                loginErrorElement.style.display = "block";
                return;
            }

            const token = await response.text();
            if (!token || token.trim() === "") {
                throw new Error("Empty token received from server.");
            }

            localStorage.setItem("jwtToken", token);

            const decodedToken = parseJwt(token);
            if (!decodedToken || !decodedToken.role) {
                throw new Error("User role is missing or token is invalid.");
            }

            const userRole = decodedToken.role.toUpperCase();

            if (userRole === "ADMIN") {
                window.location.href = "adminDashboard.html";
            } else if (userRole === "PASSENGER") {
                window.location.href = "passengerDashboard.html";
            } else {
                throw new Error("Invalid role detected: " + userRole);
            }

        } catch (error) {
            console.error("🚨 Login Error:", error);
            loginErrorElement.innerText = "⚠️ Something went wrong. Please try again.";
            loginErrorElement.style.display = "block";
        }
    });

    function parseJwt(token) {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map((c) => {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));

            return JSON.parse(jsonPayload);
        } catch (e) {
            console.error("🚨 JWT Parsing Error:", e);
            return null;
        }
    }
});
