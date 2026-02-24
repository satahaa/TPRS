package com.tprs.service;

import com.tprs.dao.NotificationDAO;
import com.tprs.model.Notification;

import java.util.List;

/**
 * Notification Service Layer
 */
public class NotificationService {
    
    private NotificationDAO notificationDAO;
    
    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }
    
    /**
     * Create a notification
     */
    public boolean createNotification(Notification notification) {
        return notificationDAO.create(notification);
    }
    
    /**
     * Send notification to a supervisor when a student submits a project
     */
    public boolean notifyProjectSubmission(int supervisorId, int studentId, String studentName, int projectId, String projectTitle) {
        Notification notification = new Notification();
        notification.setRecipientId(supervisorId);
        notification.setRecipientType("teacher");
        notification.setSenderId(studentId);
        notification.setSenderType("student");
        notification.setType("project_submitted");
        notification.setTitle("New Project Submission");
        notification.setMessage(studentName + " has submitted a new project: \"" + projectTitle + "\". Please review and take action.");
        notification.setProjectId(projectId);
        return notificationDAO.create(notification);
    }
    
    /**
     * Send notification to student when project is approved
     */
    public boolean notifyProjectApproved(int studentId, int supervisorId, String supervisorName, int projectId, String projectTitle) {
        Notification notification = new Notification();
        notification.setRecipientId(studentId);
        notification.setRecipientType("student");
        notification.setSenderId(supervisorId);
        notification.setSenderType("teacher");
        notification.setType("project_approved");
        notification.setTitle("Project Approved!");
        notification.setMessage("Your project \"" + projectTitle + "\" has been approved by " + supervisorName + ".");
        notification.setProjectId(projectId);
        return notificationDAO.create(notification);
    }
    
    /**
     * Send notification to student when project is rejected
     */
    public boolean notifyProjectRejected(int studentId, int supervisorId, String supervisorName, int projectId, String projectTitle) {
        Notification notification = new Notification();
        notification.setRecipientId(studentId);
        notification.setRecipientType("student");
        notification.setSenderId(supervisorId);
        notification.setSenderType("teacher");
        notification.setType("project_rejected");
        notification.setTitle("Project Rejected");
        notification.setMessage("Your project \"" + projectTitle + "\" has been rejected by " + supervisorName + ". Please review and resubmit.");
        notification.setProjectId(projectId);
        return notificationDAO.create(notification);
    }
    
    /**
     * Notify student about supervisor assignment
     */
    public boolean notifyAssignment(int studentId, int supervisorId, String supervisorName) {
        Notification notification = new Notification();
        notification.setRecipientId(studentId);
        notification.setRecipientType("student");
        notification.setSenderId(supervisorId);
        notification.setSenderType("teacher");
        notification.setType("assignment");
        notification.setTitle("Supervisor Assigned");
        notification.setMessage("You have been assigned to supervisor: " + supervisorName + ".");
        notification.setProjectId(null);
        return notificationDAO.create(notification);
    }
    
    /**
     * Get all notifications for a user
     */
    public List<Notification> getNotifications(int userId, String userType) {
        return notificationDAO.getByRecipient(userId, userType);
    }
    
    /**
     * Get unread notification count
     */
    public int getUnreadCount(int userId, String userType) {
        return notificationDAO.getUnreadCount(userId, userType);
    }
    
    /**
     * Mark notification as read
     */
    public boolean markAsRead(int notificationId) {
        return notificationDAO.markAsRead(notificationId);
    }
    
    /**
     * Mark all notifications as read
     */
    public boolean markAllAsRead(int userId, String userType) {
        return notificationDAO.markAllAsRead(userId, userType);
    }
}
