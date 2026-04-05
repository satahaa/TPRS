document.addEventListener('DOMContentLoaded', async () => {
    const title = document.getElementById('title');
    const subtitle = document.getElementById('subtitle');
    const headerIcon = document.getElementById('headerIcon');
    const messageBox = document.getElementById('message');
    const resetForm = document.getElementById('resetForm');
    const resetBtn = document.getElementById('resetBtn');
    const togglePasswordsIcon = document.getElementById('togglePasswordsIcon');
    const resetEmailInput = document.getElementById('resetEmail');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    let passwordsVisible = false;

    const params = new URLSearchParams(window.location.search);
    const mode = params.get('mode');
    const oobCode = params.get('oobCode');
    const apiKey = params.get('apiKey');
    const continueUrl = params.get('continueUrl');
    const langCode = params.get('lang') || params.get('langCode');

    const loginUrl = '/html/login.html';

    function redirectToLogin(query) {
        window.setTimeout(() => {
            window.location.href = query ? (loginUrl + '?' + query) : loginUrl;
        }, 1800);
    }

    function showMessage(text, type) {
        messageBox.textContent = text;
        messageBox.className = 'message show ' + type;
    }

    function showInvalidAccess(message) {
        title.textContent = 'Invalid Action Link';
        subtitle.textContent = message;
        headerIcon.textContent = 'error';
        resetForm.classList.add('hidden');
        showMessage(message, 'error');
    }

    if (togglePasswordsIcon) {
        togglePasswordsIcon.addEventListener('click', () => {
            passwordsVisible = !passwordsVisible;
            const inputType = passwordsVisible ? 'text' : 'password';

            newPasswordInput.type = inputType;
            confirmPasswordInput.type = inputType;

            togglePasswordsIcon.textContent = passwordsVisible ? 'visibility_off' : 'visibility';
            togglePasswordsIcon.setAttribute('aria-label', passwordsVisible ? 'Hide passwords' : 'Show passwords');
        });
    }

    if (langCode) {
        firebase.auth().languageCode = langCode;
    }

    if (!mode || !oobCode) {
        showInvalidAccess('This page can only be opened from a valid Firebase action email link.');
        return;
    }

    if (mode === 'verifyEmail') {
        title.textContent = 'Verifying Email';
        subtitle.textContent = 'Completing your email verification...';
        headerIcon.textContent = 'verified';

        try {
            await firebase.auth().applyActionCode(oobCode);
            showMessage('Email verified successfully. Redirecting to login...', 'success');
            subtitle.textContent = 'Your account is now verified.';
            redirectToLogin('verified=1');
        } catch (error) {
            console.error('Email verification action failed:', error, { apiKey, continueUrl });
            if (error.code === 'auth/invalid-action-code' || error.code === 'auth/expired-action-code') {
                showInvalidAccess('Verification link is invalid or expired. Please request a new verification email.');
            } else {
                showInvalidAccess('Unable to verify email right now. Please try again later.');
            }
        }
        return;
    }

    if (mode === 'resetPassword') {
        title.textContent = 'Reset Password';
        subtitle.textContent = 'Validating your reset link...';
        headerIcon.textContent = 'lock_reset';

        try {
            const email = await firebase.auth().verifyPasswordResetCode(oobCode);
            if (resetEmailInput) {
                resetEmailInput.value = email;
            }
            subtitle.textContent = 'Set a new password for ' + email;
            showMessage('Reset link is valid. Enter your new password.', 'success');
            resetForm.classList.remove('hidden');
        } catch (error) {
            console.error('Password reset code validation failed:', error, { apiKey, continueUrl });
            if (error.code === 'auth/invalid-action-code' || error.code === 'auth/expired-action-code') {
                showInvalidAccess('Reset link is invalid or expired. Please request a new one.');
            } else {
                showInvalidAccess('Unable to validate reset link right now. Please try again later.');
            }
            return;
        }

        resetForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const newPassword = newPasswordInput.value;
            const confirmPassword = confirmPasswordInput.value;

            if (newPassword.length < 6) {
                showMessage('Password must be at least 6 characters long.', 'error');
                return;
            }

            if (newPassword !== confirmPassword) {
                showMessage('Passwords do not match.', 'error');
                return;
            }

            const originalBtn = resetBtn.innerHTML;
            resetBtn.innerHTML = '<span class="material-icons">hourglass_empty</span> Updating...';
            resetBtn.disabled = true;

            try {
                await firebase.auth().confirmPasswordReset(oobCode, newPassword);
                showMessage('Password updated successfully. Redirecting to login...', 'success');
                subtitle.textContent = 'Password reset complete.';
                resetForm.classList.add('hidden');
                redirectToLogin('reset=1');
            } catch (error) {
                console.error('Password reset confirm failed:', error);
                if (error.code === 'auth/invalid-action-code' || error.code === 'auth/expired-action-code') {
                    showMessage('Reset link expired or invalid. Please request a new reset email.', 'error');
                } else if (error.code === 'auth/weak-password') {
                    showMessage('Choose a stronger password and try again.', 'error');
                } else {
                    showMessage('Unable to reset password right now. Please try again.', 'error');
                }
                resetBtn.disabled = false;
            } finally {
                resetBtn.innerHTML = originalBtn;
            }
        });
        return;
    }

    showInvalidAccess('Unsupported action mode: ' + mode);
});
