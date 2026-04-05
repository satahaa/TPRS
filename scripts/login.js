document.addEventListener('DOMContentLoaded', () => {
    const passwordInput = document.getElementById('password');
    const toggleIcon = document.getElementById('toggleIcon');
    const togglePasswordBtn = document.querySelector('.toggle-password');
    const loginForm = document.getElementById('loginForm');
    const errorMessage = document.getElementById('errorMessage');
    const successMessage = document.getElementById('successMessage');

    const loginParams = new URLSearchParams(window.location.search);
    if (successMessage) {
        if (loginParams.get('verified') === '1') {
            successMessage.textContent = 'Email verified successfully. You can now sign in.';
            successMessage.classList.add('show');
        } else if (loginParams.get('reset') === '1') {
            successMessage.textContent = 'Password updated successfully. Please sign in with your new password.';
            successMessage.classList.add('show');
        }
    }
    
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
                // Admin login is backend-only. Non-admin users use Firebase + token login.
                if (email.toLowerCase() === 'admin@tprs.com') {
                    if (typeof TPRSApi !== 'undefined' && typeof TPRSApi.login === 'function') {
                        const adminResponse = await TPRSApi.login(email, password);
                        if (adminResponse && adminResponse.success) {
                            if (adminResponse.user) {
                                TPRSApi.saveSession(adminResponse.user, adminResponse.userType || 'admin');
                            }
                            window.location.href = adminResponse.redirect || '/html/admin-dashboard.html';
                            return;
                        }
                        errorMessage.textContent = (adminResponse && adminResponse.message) || 'Invalid admin credentials.';
                        errorMessage.classList.add('show');
                        submitBtn.innerHTML = originalBtnHtml;
                        submitBtn.disabled = false;
                        return;
                    }
                    errorMessage.textContent = 'System error: API not loaded properly.';
                    errorMessage.classList.add('show');
                    submitBtn.innerHTML = originalBtnHtml;
                    submitBtn.disabled = false;
                    return;
                }

                // For supervisor bootstrap flow, allow backend login with default password
                // if Firebase credentials are out of sync.
                if (password === 'csembstu' && typeof TPRSApi !== 'undefined' && typeof TPRSApi.login === 'function') {
                    const legacyResponse = await TPRSApi.login(email, password);
                    if (legacyResponse && legacyResponse.success && legacyResponse.userType === 'teacher') {
                        if (legacyResponse.user) {
                            TPRSApi.saveSession(legacyResponse.user, 'teacher');
                        }
                        window.location.href = legacyResponse.redirect || '/html/supervisor-dashboard.html';
                        return;
                    }
                }

                // Normal logic: Authenticate with Firebase first
                const userCredential = await firebase.auth().signInWithEmailAndPassword(email, password);
                await userCredential.user.reload();
                const idToken = await userCredential.user.getIdToken(true);
                
                // 2. Send the idToken to the Java backend via api.js
                if (typeof TPRSApi !== 'undefined' && typeof TPRSApi.loginWithToken === 'function') {
                    const response = await TPRSApi.loginWithToken(idToken, password);
                    
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

                // If Firebase rejects credentials, try backend fallback for default supervisor password.
                if ((err.code === 'auth/invalid-credential' || err.code === 'auth/wrong-password' || err.code === 'auth/user-not-found')
                    && password === 'csembstu'
                    && typeof TPRSApi !== 'undefined'
                    && typeof TPRSApi.login === 'function') {
                    try {
                        const fallbackResponse = await TPRSApi.login(email, password);
                        if (fallbackResponse && fallbackResponse.success && fallbackResponse.userType === 'teacher') {
                            if (fallbackResponse.user) {
                                TPRSApi.saveSession(fallbackResponse.user, 'teacher');
                            }
                            window.location.href = fallbackResponse.redirect || '/html/supervisor-dashboard.html';
                            return;
                        }
                    } catch (fallbackErr) {
                        console.warn('Supervisor fallback login failed:', fallbackErr);
                    }
                }
                
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
                if (successMessage) {
                    successMessage.textContent = 'Loading...';
                    successMessage.classList.add('show');
                    successMessage.style.color = '#b5b5cc';
                    successMessage.style.marginTop = '10px';
                    successMessage.style.fontSize = '14px';
                }

                // Ask backend to send custom branded reset email with Firebase action link.
                if (typeof TPRSApi !== 'undefined' && typeof TPRSApi.notifyForgotPassword === 'function') {
                    const resetResponse = await TPRSApi.notifyForgotPassword(email);
                    if (!resetResponse || !resetResponse.success) {
                        throw new Error((resetResponse && resetResponse.message) || 'Failed to send reset email.');
                    }
                } else {
                    throw new Error('System error: API not loaded properly.');
                }
                
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
                if (successMessage) {
                    successMessage.classList.remove('show');
                }
                if (errorMessage) {
                    errorMessage.textContent = error.message || 'Failed to send reset email. Please try again.';
                    errorMessage.classList.add('show');
                } else {
                    alert('Failed to send reset email: ' + error.message);
                }
            }
        });
    }
});
