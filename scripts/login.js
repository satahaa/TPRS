document.addEventListener('DOMContentLoaded', () => {
    const passwordInput = document.getElementById('password');
    const toggleIcon = document.getElementById('toggleIcon');
    const togglePasswordBtn = document.querySelector('.toggle-password');
    const loginForm = document.getElementById('loginForm');
    const errorMessage = document.getElementById('errorMessage');
    
    // Toggle Password Visibility
    if (togglePasswordBtn) {
        togglePasswordBtn.addEventListener('click', () => {
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                toggleIcon.textContent = 'visibility_off';
            } else {
                passwordInput.type = 'password';
                toggleIcon.textContent = 'visibility';
            }
        });
    }
    
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault(); // Prevent URL parameter GET submission
            
            const email = document.getElementById('email').value.trim();
            const password = passwordInput.value;
            const submitBtn = loginForm.querySelector('button[type="submit"]');
            
            if (!email || !password) {
                errorMessage.textContent = 'Please enter both email and password.';
                errorMessage.classList.add('show');
                return;
            }
            
            // UI indicate loading
            const originalBtnHtml = submitBtn.innerHTML;
            submitBtn.innerHTML = '<span class="material-icons">hourglass_empty</span> Logging in...';
            submitBtn.disabled = true;
            errorMessage.classList.remove('show');

            try {
                // Determine if we should attempt a direct backend login first (for Admin / Legacy)
                let isBackendLogin = false;
                let response = null;

                // Try direct backend login first to cleanly support the admin account
                try {
                    if (typeof TPRSApi !== 'undefined' && typeof TPRSApi.login === 'function') {
                        response = await TPRSApi.login(email, password);
                    }
                } catch (e) {
                    console.warn("Direct backend login check failed:", e);
                }

                if (response) {
                    if (response.success) {
                        isBackendLogin = true;
                    } else if (response.isAdminEmail || email === 'admin@tprs.com' || response.isTeacherError) {
                        // The email strictly matches the admin email in backend configuration, 
                        // but password was wrong, or network failed. Do not invoke Firebase. Break immediately.
                        // Or it's a teacher who provided correct backend password but isn't authorized.
                        errorMessage.textContent = response.message || 'Incorrect Credentials';
                        errorMessage.classList.add('show');
                        submitBtn.innerHTML = originalBtnHtml;
                        submitBtn.disabled = false;
                        return;
                    }
                }

                if (isBackendLogin) {
                    // Handled entirely by the backend (e.g. Admin)
                    if (response.user) {
                        TPRSApi.saveSession(response.user, response.userType || 'admin');
                    }
                    if (response.redirect) {
                        window.location.href = response.redirect;
                    } else {
                        window.location.href = '/html/admin-dashboard.html';
                    }
                    return;
                }

                // Normal logic: Authenticate with Firebase first
                const userCredential = await firebase.auth().signInWithEmailAndPassword(email, password);
                const idToken = await userCredential.user.getIdToken();
                
                // 2. Send the idToken to the Java backend via api.js
                if (typeof TPRSApi !== 'undefined' && typeof TPRSApi.loginWithToken === 'function') {
                    const response = await TPRSApi.loginWithToken(idToken);
                    
                    if (response && response.success) {
                        // 3. Save the session!
                        if (response.user) {
                            TPRSApi.saveSession(response.user, response.userType || 'student');
                        }

                        // Success, redirect based on response
                        if (response.redirect) {
                            window.location.href = response.redirect;
                        } else {
                            window.location.href = '/html/home.html'; // Default fallback
                        }
                    } else {
                        // Failure from backend
                        errorMessage.textContent = response.message || 'Invalid account details or unauthorized.';
                        errorMessage.classList.add('show');
                        submitBtn.innerHTML = originalBtnHtml;
                        submitBtn.disabled = false;
                        
                        // Sign out of Firebase if backend rejects
                        try {
                            await firebase.auth().signOut();
                        } catch (signOutErr) {
                            console.error("Error signing out:", signOutErr);
                        }
                    }
                } else {
                    console.error("TPRSApi not found or loginWithToken function missing.");
                    errorMessage.textContent = 'System error: API not loaded properly.';
                    errorMessage.classList.add('show');
                    submitBtn.innerHTML = originalBtnHtml;
                    submitBtn.disabled = false;
                }

            } catch (err) {
                console.error("Login process error:", err);
                
                // Handle Firebase-specific errors gracefully
                if (err.code === 'auth/user-not-found' || err.code === 'auth/wrong-password' || err.code === 'auth/invalid-credential') {
                    errorMessage.textContent = 'Incorrect Credentials';
                } else {
                    errorMessage.textContent = 'Authentication failed. Please check your credentials.';
                }
                
                errorMessage.classList.add('show');
                submitBtn.innerHTML = originalBtnHtml;
                submitBtn.disabled = false;
            }
        });
    }

    // Forgot Password Flow
    const forgotPassLink = document.getElementById('forgotPassLink');
    if (forgotPassLink) {
        forgotPassLink.addEventListener('click', async (e) => {
            e.preventDefault();
            
            const email = document.getElementById('email').value.trim();
            const successMessage = document.getElementById('successMessage'); // Assume this exists in login.html or we'll create it or use alert
            
            // clear both messages first
            if(errorMessage) errorMessage.classList.remove('show');
            if(successMessage) successMessage.classList.remove('show');

            if (!email) {
                if(errorMessage) {
                    errorMessage.textContent = 'Please enter your email address to reset password.';
                    errorMessage.classList.add('show');
                } else {
                    alert('Please enter your email address to reset password.');
                }
                return;
            }

            try {
                // Send password reset email using Firebase Auth
                await firebase.auth().sendPasswordResetEmail(email);
                
                if (successMessage) {
                    successMessage.textContent = 'Password reset email sent! Please check your inbox.';
                    successMessage.classList.add('show');
                    successMessage.style.color = 'green';
                    successMessage.style.marginTop = '10px';
                    successMessage.style.fontSize = '14px';
                } else {
                    alert('Password reset email sent! Please check your inbox.');
                }
            } catch (error) {
                console.error('Password reset error:', error);
                if (errorMessage) {
                    if (error.code === 'auth/user-not-found') {
                        errorMessage.textContent = 'No account found with this email.';
                    } else if (error.code === 'auth/invalid-email') {
                        errorMessage.textContent = 'Please enter a valid email address.';
                    } else {
                        errorMessage.textContent = 'Failed to send reset email. Please try again.';
                    }
                    errorMessage.classList.add('show');
                } else {
                    alert('Failed to send reset email: ' + error.message);
                }
            }
        });
    }
});
