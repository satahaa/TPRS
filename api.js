/**
 * TPRS API Service
 * Handles all API calls to the Java backend
 */

const API_BASE_URL = 'http://localhost:8080/tprs/api';

const TPRSApi = {
    
    // =====================================================
    // AUTHENTICATION APIs
    // =====================================================
    
    /**
     * Login user (auto-detects role by email)
     * @param {string} email - User email
     * @param {string} password - User password
     * @returns {Promise} - API response with userType and redirect
     */
    async login(email, password) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });
            return await response.json();
        } catch (error) {
            console.error('Login error:', error);
            return { success: false, message: 'Network error. Please check your connection.' };
        }
    },
    
    /**
     * Register a new student
     * @param {Object} studentData - Student registration data
     * @returns {Promise} - API response
     */
    async registerStudent(studentData) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(studentData)
            });
            return await response.json();
        } catch (error) {
            console.error('Registration error:', error);
            return { success: false, message: 'Network error. Please check your connection.' };
        }
    },
    
    /**
     * Register a new teacher
     * @param {Object} teacherData - Teacher registration data
     * @returns {Promise} - API response
     */
    async registerTeacher(teacherData) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/register-teacher`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(teacherData)
            });
            return await response.json();
        } catch (error) {
            console.error('Registration error:', error);
            return { success: false, message: 'Network error. Please check your connection.' };
        }
    },
    
    // =====================================================
    // PROJECT APIs
    // =====================================================
    
    /**
     * Get all projects
     * @param {Object} filters - Optional filters (status, department, search, limit)
     * @returns {Promise} - API response with projects list
     */
    async getProjects(filters = {}) {
        try {
            const queryParams = new URLSearchParams(filters).toString();
            const url = queryParams ? `${API_BASE_URL}/projects?${queryParams}` : `${API_BASE_URL}/projects`;
            
            const response = await fetch(url);
            return await response.json();
        } catch (error) {
            console.error('Get projects error:', error);
            return { success: false, message: 'Failed to fetch projects.' };
        }
    },
    
    /**
     * Get recent projects for dashboard
     * @param {number} limit - Number of projects to fetch
     * @returns {Promise} - API response with recent projects
     */
    async getRecentProjects(limit = 10) {
        try {
            const response = await fetch(`${API_BASE_URL}/projects/recent?limit=${limit}`);
            return await response.json();
        } catch (error) {
            console.error('Get recent projects error:', error);
            return { success: false, message: 'Failed to fetch recent projects.' };
        }
    },
    
    /**
     * Get project by ID
     * @param {number} projectId - Project ID
     * @returns {Promise} - API response with project details
     */
    async getProject(projectId) {
        try {
            const response = await fetch(`${API_BASE_URL}/projects/${projectId}`);
            return await response.json();
        } catch (error) {
            console.error('Get project error:', error);
            return { success: false, message: 'Failed to fetch project.' };
        }
    },
    
    /**
     * Get projects by student ID
     * @param {number} studentId - Student ID
     * @returns {Promise} - API response with student's projects
     */
    async getProjectsByStudent(studentId) {
        try {
            const response = await fetch(`${API_BASE_URL}/projects?studentId=${studentId}`);
            return await response.json();
        } catch (error) {
            console.error('Get student projects error:', error);
            return { success: false, message: 'Failed to fetch projects.' };
        }
    },
    
    /**
     * Submit a new project/thesis
     * @param {Object} projectData - Project data
     * @param {File} file - Optional file to upload
     * @returns {Promise} - API response
     */
    async submitProject(projectData, file = null) {
        try {
            let response;
            
            if (file) {
                // Use FormData for file upload
                const formData = new FormData();
                Object.keys(projectData).forEach(key => {
                    formData.append(key, projectData[key]);
                });
                formData.append('file', file);
                
                response = await fetch(`${API_BASE_URL}/projects`, {
                    method: 'POST',
                    body: formData
                });
            } else {
                response = await fetch(`${API_BASE_URL}/projects`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(projectData)
                });
            }
            
            return await response.json();
        } catch (error) {
            console.error('Submit project error:', error);
            return { success: false, message: 'Failed to submit project.' };
        }
    },
    
    /**
     * Update project
     * @param {number} projectId - Project ID
     * @param {Object} projectData - Updated project data
     * @returns {Promise} - API response
     */
    async updateProject(projectId, projectData) {
        try {
            const response = await fetch(`${API_BASE_URL}/projects/${projectId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(projectData)
            });
            return await response.json();
        } catch (error) {
            console.error('Update project error:', error);
            return { success: false, message: 'Failed to update project.' };
        }
    },
    
    /**
     * Approve project
     * @param {number} projectId - Project ID
     * @returns {Promise} - API response
     */
    async approveProject(projectId) {
        try {
            const response = await fetch(`${API_BASE_URL}/projects/${projectId}/approve`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            return await response.json();
        } catch (error) {
            console.error('Approve project error:', error);
            return { success: false, message: 'Failed to approve project.' };
        }
    },
    
    /**
     * Reject project
     * @param {number} projectId - Project ID
     * @param {string} reason - Optional rejection reason
     * @returns {Promise} - API response
     */
    async rejectProject(projectId, reason = '') {
        try {
            const body = reason ? { reason } : {};
            const response = await fetch(`${API_BASE_URL}/projects/${projectId}/reject`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });
            return await response.json();
        } catch (error) {
            console.error('Reject project error:', error);
            return { success: false, message: 'Failed to reject project.' };
        }
    },
    
    /**
     * Delete project
     * @param {number} projectId - Project ID
     * @returns {Promise} - API response
     */
    async deleteProject(projectId) {
        try {
            const response = await fetch(`${API_BASE_URL}/projects/${projectId}`, {
                method: 'DELETE'
            });
            return await response.json();
        } catch (error) {
            console.error('Delete project error:', error);
            return { success: false, message: 'Failed to delete project.' };
        }
    },
    
    /**
     * Search projects
     * @param {string} keyword - Search keyword
     * @returns {Promise} - API response with matching projects
     */
    async searchProjects(keyword) {
        try {
            const response = await fetch(`${API_BASE_URL}/projects?search=${encodeURIComponent(keyword)}`);
            return await response.json();
        } catch (error) {
            console.error('Search projects error:', error);
            return { success: false, message: 'Failed to search projects.' };
        }
    },
    
    // =====================================================
    // DASHBOARD APIs
    // =====================================================
    
    /**
     * Get dashboard statistics
     * @returns {Promise} - API response with statistics
     */
    async getDashboardStats() {
        try {
            const response = await fetch(`${API_BASE_URL}/dashboard/stats`);
            return await response.json();
        } catch (error) {
            console.error('Get dashboard stats error:', error);
            return { success: false, message: 'Failed to fetch dashboard statistics.' };
        }
    },
    
    /**
     * Get recent projects for dashboard
     * @param {number} limit - Number of recent projects
     * @returns {Promise} - API response with recent projects
     */
    async getDashboardRecent(limit = 10) {
        try {
            const response = await fetch(`${API_BASE_URL}/dashboard/recent?limit=${limit}`);
            return await response.json();
        } catch (error) {
            console.error('Get recent projects error:', error);
            return { success: false, message: 'Failed to fetch recent projects.' };
        }
    },
    
    /**
     * Get projects count by department
     * @returns {Promise} - API response with department statistics
     */
    async getProjectsByDepartment() {
        try {
            const response = await fetch(`${API_BASE_URL}/dashboard/by-department`);
            return await response.json();
        } catch (error) {
            console.error('Get department stats error:', error);
            return { success: false, message: 'Failed to fetch department statistics.' };
        }
    },
    
    // =====================================================
    // NOTIFICATION APIs
    // =====================================================
    
    /**
     * Get notifications for a user
     * @param {number} userId - User ID
     * @param {string} userType - 'student' or 'teacher'
     * @returns {Promise} - API response with notifications
     */
    async getNotifications(userId, userType) {
        try {
            const response = await fetch(`${API_BASE_URL}/notifications?userId=${userId}&userType=${userType}`);
            return await response.json();
        } catch (error) {
            console.error('Get notifications error:', error);
            return { success: false, message: 'Failed to fetch notifications.' };
        }
    },
    
    /**
     * Get unread notification count
     * @param {number} userId - User ID
     * @param {string} userType - 'student' or 'teacher'
     * @returns {Promise} - API response with count
     */
    async getUnreadNotificationCount(userId, userType) {
        try {
            const response = await fetch(`${API_BASE_URL}/notifications/count?userId=${userId}&userType=${userType}`);
            return await response.json();
        } catch (error) {
            console.error('Get unread count error:', error);
            return { success: false, message: 'Failed to fetch unread count.' };
        }
    },
    
    /**
     * Mark a notification as read
     * @param {number} notificationId - Notification ID
     * @returns {Promise} - API response
     */
    async markNotificationRead(notificationId) {
        try {
            const response = await fetch(`${API_BASE_URL}/notifications/${notificationId}`, {
                method: 'PUT'
            });
            return await response.json();
        } catch (error) {
            console.error('Mark notification read error:', error);
            return { success: false, message: 'Failed to mark notification as read.' };
        }
    },
    
    /**
     * Mark all notifications as read
     * @param {number} userId - User ID
     * @param {string} userType - 'student' or 'teacher'
     * @returns {Promise} - API response
     */
    async markAllNotificationsRead(userId, userType) {
        try {
            const response = await fetch(`${API_BASE_URL}/notifications/read-all`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ userId, userType })
            });
            return await response.json();
        } catch (error) {
            console.error('Mark all read error:', error);
            return { success: false, message: 'Failed to mark all notifications as read.' };
        }
    },
    
    // =====================================================
    // SUPERVISOR ASSIGNMENT APIs
    // =====================================================
    
    /**
     * Get students assigned to a supervisor
     * @param {number} supervisorId - Teacher/Supervisor ID
     * @returns {Promise} - API response with assigned students
     */
    async getAssignedStudents(supervisorId) {
        try {
            const response = await fetch(`${API_BASE_URL}/assignments/by-supervisor?supervisorId=${supervisorId}`);
            return await response.json();
        } catch (error) {
            console.error('Get assigned students error:', error);
            return { success: false, message: 'Failed to fetch assigned students.' };
        }
    },
    
    /**
     * Get unassigned students (not assigned to this supervisor)
     * @param {number} supervisorId - Teacher/Supervisor ID
     * @returns {Promise} - API response with unassigned students
     */
    async getUnassignedStudents(supervisorId) {
        try {
            const response = await fetch(`${API_BASE_URL}/assignments/unassigned?supervisorId=${supervisorId}`);
            return await response.json();
        } catch (error) {
            console.error('Get unassigned students error:', error);
            return { success: false, message: 'Failed to fetch unassigned students.' };
        }
    },
    
    /**
     * Assign a student to the supervisor
     * @param {number} supervisorId - Teacher/Supervisor ID
     * @param {number} studentId - Student ID
     * @returns {Promise} - API response
     */
    async assignStudent(supervisorId, studentId, year, semester) {
        try {
            const response = await fetch(`${API_BASE_URL}/assignments`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ supervisorId, studentId, year: year || null, semester: semester || null })
            });
            return await response.json();
        } catch (error) {
            console.error('Assign student error:', error);
            return { success: false, message: 'Failed to assign student.' };
        }
    },
    
    /**
     * Unassign a student from the supervisor
     * @param {number} supervisorId - Teacher/Supervisor ID
     * @param {number} studentId - Student ID
     * @returns {Promise} - API response
     */
    async unassignStudent(supervisorId, studentId) {
        try {
            const response = await fetch(`${API_BASE_URL}/assignments?supervisorId=${supervisorId}&studentId=${studentId}`, {
                method: 'DELETE'
            });
            return await response.json();
        } catch (error) {
            console.error('Unassign student error:', error);
            return { success: false, message: 'Failed to unassign student.' };
        }
    },
    
    /**
     * Get supervisors assigned to a student
     * @param {number} studentId - Student ID
     * @returns {Promise} - API response with supervisors
     */
    async getSupervisorsForStudent(studentId, year, semester) {
        try {
            let url = `${API_BASE_URL}/assignments/by-student?studentId=${studentId}`;
            if (year && semester) {
                url += `&year=${encodeURIComponent(year)}&semester=${encodeURIComponent(semester)}`;
            }
            const response = await fetch(url);
            return await response.json();
        } catch (error) {
            console.error('Get supervisors error:', error);
            return { success: false, message: 'Failed to fetch supervisors.' };
        }
    },
    
    // =====================================================
    // SESSION MANAGEMENT
    // =====================================================
    
    /**
     * Save user session
     * @param {Object} user - User data
     * @param {string} userType - 'student' or 'teacher'
     */
    saveSession(user, userType) {
        sessionStorage.setItem('isLoggedIn', 'true');
        sessionStorage.setItem('userType', userType);
        sessionStorage.setItem('currentUser', JSON.stringify(user));
        sessionStorage.setItem('userEmail', user.email);
    },
    
    /**
     * Get current user session
     * @returns {Object|null} - Current user data or null
     */
    getCurrentUser() {
        const userStr = sessionStorage.getItem('currentUser');
        return userStr ? JSON.parse(userStr) : null;
    },
    
    /**
     * Check if user is logged in
     * @returns {boolean}
     */
    isLoggedIn() {
        return sessionStorage.getItem('isLoggedIn') === 'true';
    },
    
    /**
     * Get user type
     * @returns {string|null} - 'student' or 'teacher' or null
     */
    getUserType() {
        return sessionStorage.getItem('userType');
    },
    
    /**
     * Logout user
     */
    logout() {
        sessionStorage.removeItem('isLoggedIn');
        sessionStorage.removeItem('userType');
        sessionStorage.removeItem('currentUser');
        sessionStorage.removeItem('userEmail');
    },
    
    /**
     * Require authentication - redirect to login if not logged in
     */
    requireAuth() {
        if (!this.isLoggedIn()) {
            window.location.href = 'login.html';
            return false;
        }
        return true;
    }
};

// Export for use in other files
if (typeof module !== 'undefined' && module.exports) {
    module.exports = TPRSApi;
}
